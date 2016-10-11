package chefeclipseplugin.wizards

import java.net.URI

import scala.annotation.elidable
import scala.annotation.elidable.ASSERTION
import scala.sys.process.Process
import scala.sys.process.stringSeqToProcess

import org.eclipse.core.resources.IFolder
import org.eclipse.core.resources.IProject
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.runtime.CoreException

import scala.concurrent.ExecutionContext.Implicits.global

import chefeclipseplugin.nature.ChefNature
import java.io.File
import java.lang.reflect.InvocationTargetException
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.jface.operation.IRunnableWithProgress
import org.eclipse.core.runtime.SubMonitor
import scala.sys.process.ProcessLogger
import org.eclipse.core.resources.IResource
import org.eclipse.core.runtime.IStatus
import org.eclipse.ui.ide.IDE
import org.eclipse.ui.IWorkbenchWindow
import org.eclipse.ui.IWorkbenchPage
import org.eclipse.ui.PlatformUI
import org.eclipse.core.runtime.IPath
import org.eclipse.swt.widgets.Display
import scala.concurrent.Future

object ChefUtil {

  /*
   * -g GENERATOR_COOKBOOK_PATH, --generator-cookbook GENERATOR_COOKBOOK_PATH
   * The path at which a cookbook named code_generator is located. This cookbook is used by the chef generate subcommands to generate cookbooks, cookbook files, templates, attribute files, and so on. Default value: lib/chef-dk/skeletons, under which is the default code_generator cookbook that is included as part of the Chef development kit.
   * -b, --berks Create a Berksfile in the cookbook. Default value: enabled. This is disabled if the --policy option is given.
   * -C COPYRIGHT, --copyright COPYRIGHT Specify the copyright holder for copyright notices in generated files. Default value: The Authors
   * -d, --delivery Generate a delivery config file and build cookbook inside the new cookbook. Default value: disabled.
   * -m EMAIL, --email EMAIL Specify the email address of the author. Default value: you@example.com.
   * -a KEY=VALUE, --generator-arg KEY=VALUE
   * Sets a property named KEY to the given VALUE on the generator context object in the generator cookbook. This allows custom generator cookbooks to accept optional user input on the command line.
   * -I LICENSE, --license LICENSE
   * Sets the license. Valid values are all_rights, apache2, mit, gplv2, or gplv3. Default value: all_rights.   *
*/

  def createCookbook(projectName: String, location: URI, cookbookInfo: CookbookInfo): IRunnableWithProgress = {
    assert(projectName != null)
    assert(projectName.trim.nonEmpty)

    new IRunnableWithProgress() {
      def run(monitor: IProgressMonitor) {
        try {
          val project = createBaseProject(projectName, location)
          addNature(project)
          val command = Seq("/usr/bin/chef", "generate", "cookbook", projectName,
            "-C", cookbookInfo.copyright,
            "-m", cookbookInfo.email,
            "-I", cookbookInfo.license.toString) ++
            (if (cookbookInfo.berks) Some("-b") else None) ++
            (if (cookbookInfo.deliveryFile) Some("-d") else None)

          val proc = Process(command, (new File(location.getPath)).getParentFile)
          val ranit = proc ! ProcessLogger(str => {
            println(str)
            monitor.setTaskName(str)
          })
          project.refreshLocal(IResource.DEPTH_INFINITE, monitor)
          if (ranit != 0) {
            System.err.println(s"$command returned something not zero")
            throw new Error(" Chef generate failed")
          }

        } catch {
          case e: CoreException =>
            e.printStackTrace()
            throw new InvocationTargetException(e)
        } finally {
          monitor.done()
        }
      }
    }
  }

  private def project(name: String) = ResourcesPlugin.getWorkspace.getRoot.getProject(name)

  private def createBaseProject(projectName: String, location: URI): IProject = {
    val project = ResourcesPlugin.getWorkspace.getRoot.getProject(projectName)
    if (!project.exists()) {
      val description = project.getWorkspace.newProjectDescription(projectName)
      val projectLocation = {
        if (location != null && ResourcesPlugin.getWorkspace().getRoot().getLocationURI().equals(location)) {
          null;
        } else {
          location
        }
      }

      description.setLocationURI(projectLocation);
      try {
        project.create(description, null);
        if (!project.isOpen()) {
          project.open(null)
        }
      } catch {
        case e: CoreException => e.printStackTrace
      }

    }
    project
  }

  private def addNature(project: IProject) = {
    if (!project.hasNature(ChefNature.NATURE_ID)) {
      val description = project.getDescription
      description.setNatureIds(description.getNatureIds :+ ChefNature.NATURE_ID)
      project.setDescription(description, null)
    }
  }

  private def createResource(project: IProject, command: Seq[String], pathsToOpen: IPath*) = {
    new IRunnableWithProgress() {
      def run(monitor: IProgressMonitor) {
        try {
          val proc = Process(command, new File(project.getRawLocation.toPortableString))
          val ranit = proc ! ProcessLogger(str => {
            println(str)
            monitor.setTaskName(str)
          })
          project.refreshLocal(IResource.DEPTH_INFINITE, monitor)
          if (ranit != 0) {
            System.err.println(s"$command returned something not zero")
            throw new Error(" Chef generate failed")
          }

          //open the resource file we just created
          //wait for file to exist
          pathsToOpen.foreach { path =>
            {
              Display.getDefault().asyncExec(new Runnable() {
                override def run() = {
                  var reconnectAttempt: Int = 0
                  val reconnectDecay: Double = 1.5
                  val maxReconnectInterval: Long = 60000
                  val minReconnectInterval: Long = 200
                  val maxReconnectAttempts: Option[Int] = Some(10)
                  val reconnectInterval: Int = 1000

                  val window = PlatformUI.getWorkbench.getActiveWorkbenchWindow
                  val openMe = ResourcesPlugin.getWorkspace.getRoot.getFileForLocation(path)

                  while (!path.toFile().exists() && (maxReconnectAttempts.isEmpty || reconnectAttempt <= maxReconnectAttempts.get)) {
                    import Math._

                    //This algorithm for figuring out the reconnect time is called "binary exponential backoff"
                    val apow = pow(reconnectDecay, reconnectAttempt)
                    val minlong = min(Long.MaxValue, apow)
                    val rand = random() * minlong
                    val interval = minReconnectInterval + (reconnectInterval * rand)
                    val timeoutInterval = min(maxReconnectInterval, interval)

                    println(s"File ${openMe.toString} doesn't exist, waiting ${timeoutInterval.toLong} more to try again")

                    Thread.sleep(timeoutInterval.toLong)
                    reconnectAttempt = reconnectAttempt + 1
                  }
                  if (reconnectAttempt > maxReconnectAttempts.get) {
                    System.err.println(s"We waited a long time for file ${openMe.toString} but apparently it was never cretaed")
                  } else {
                    println(s"File ${openMe.toString} now exists, opening it")
                    IDE.openEditor(window.getActivePage, openMe)
                  }
                }
              })
            }
          }

        } catch {
          case e: CoreException =>
            e.printStackTrace()
            throw new InvocationTargetException(e)
          case t: Throwable =>
            t.printStackTrace()
            throw t
        } finally {
          monitor.done()
        }
      }
    }
  }

  def createRecipe(project: IProject, info: RecipeInfo) = {
    assert(project != null)
    assert(info != null)
    assert(info.name != null)
    assert(info.name.trim.nonEmpty)

    val command = Seq("/usr/bin/chef", "generate", "recipe", info.name,
      "-C", info.copyright,
      "-m", info.email,
      "-I", info.license.toString)
    val pathToOpen = project.getLocation.append(s"/recipes/${info.name}.rb")

    createResource(project, command, pathToOpen)
  }

  def createAttribute(project: IProject, info: AttributeInfo) = {
    assert(project != null)
    assert(info != null)
    assert(info.name != null)
    assert(info.name.trim.nonEmpty)

    val command = Seq("/usr/bin/chef", "generate", "attribute", info.name)
    val pathToOpen = project.getLocation.append(s"/attributes/${info.name}.rb")

    createResource(project, command, pathToOpen)
  }

  def createTemplate(project: IProject, info: TemplateInfo) = {
    assert(project != null)
    assert(info != null)
    assert(info.name != null)
    assert(info.name.trim.nonEmpty)

    val command = Seq("/usr/bin/chef", "generate", "template", info.name)
    val pathToOpen = project.getLocation.append(s"/templates/default/${info.name}")

    createResource(project, command, pathToOpen)
  }

  def createFile(project: IProject, info: FileInfo) = {
    assert(project != null)
    assert(info != null)
    assert(info.name != null)
    assert(info.name.trim.nonEmpty)

    val command = Seq("/usr/bin/chef", "generate", "file", info.name)
    val pathToOpen = project.getLocation.append(s"/files/default/${info.name}")

    createResource(project, command, pathToOpen)
  }

  def createLwrp(project: IProject, info: LwrpInfo) = {
    assert(project != null)
    assert(info != null)
    assert(info.name != null)
    assert(info.name.trim.nonEmpty)

    val command = Seq("/usr/bin/chef", "generate", "lwrp", info.name,
      "-C", info.copyright,
      "-m", info.email,
      "-I", info.license.toString)

    createResource(project, command,
      project.getLocation.append(s"/providers/${info.name}.rb"),
      project.getLocation.append(s"/resources/${info.name}.rb"))
  }
}