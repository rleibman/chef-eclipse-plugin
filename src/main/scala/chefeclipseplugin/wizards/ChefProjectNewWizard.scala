package chefeclipseplugin.wizards

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.reflect.InvocationTargetException

import org.eclipse.core.resources.IContainer
import org.eclipse.core.resources.IResource
import org.eclipse.core.runtime.CoreException
import org.eclipse.core.runtime.IConfigurationElement
import org.eclipse.core.runtime.IExecutableExtension
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.Status
import org.eclipse.jface.dialogs.MessageDialog
import org.eclipse.jface.viewers.ISelection
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.jface.wizard.Wizard
import org.eclipse.jface.wizard.WizardPage
import org.eclipse.swt.SWT
import org.eclipse.swt.events.ModifyEvent
import org.eclipse.swt.events.ModifyListener
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Combo
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Text
import org.eclipse.ui.INewWizard
import org.eclipse.ui.IWorkbench
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard

class ChefProjectNewWizard extends Wizard() with INewWizard with IExecutableExtension {
  class CookbookInfoWizardPage(private var selection: ISelection) extends WizardPage("Chef Information") {

    private var maintainerEmailText: Text = _
    private var copyrightText: Text = _
    private var berksBoolean: Button = _
    private var deliverFileBoolean: Button = _
    private var licenseCombo: Combo = _

    setTitle("Chef Cookbook Project")
    setDescription("This wizard creates a new chef cookbook project.")

    override def createControl(parent: Composite) = {
      val container = new Composite(parent, SWT.NULL)
      val layout = new GridLayout()
      container.setLayout(layout)
      layout.numColumns = 3
      layout.verticalSpacing = 9

      var label = new Label(container, SWT.NULL)
      var gd = new GridData(GridData.FILL_HORIZONTAL)
      label = new Label(container, SWT.NULL)
      label.setText("&Maintainer Email Text:")
      gd = new GridData(GridData.FILL_HORIZONTAL)
      maintainerEmailText = new Text(container, SWT.BORDER | SWT.SINGLE)
      maintainerEmailText.setLayoutData(gd)
      maintainerEmailText.addModifyListener(new ModifyListener() {
        def modifyText(e: ModifyEvent) {
          dialogChanged()
        }
      })

      label = new Label(container, SWT.NULL)
      gd = new GridData(GridData.FILL_HORIZONTAL)
      label = new Label(container, SWT.NULL)
      label.setText("&Copyright Text:")
      gd = new GridData(GridData.FILL_HORIZONTAL)
      copyrightText = new Text(container, SWT.BORDER | SWT.SINGLE)
      copyrightText.setLayoutData(gd)
      copyrightText.addModifyListener(new ModifyListener() {
        def modifyText(e: ModifyEvent) {
          dialogChanged()
        }
      })

      label = new Label(container, SWT.NULL)
      gd = new GridData(GridData.FILL_HORIZONTAL)
      label = new Label(container, SWT.NULL)
      label.setText("Use &Berkshelf:")
      gd = new GridData(GridData.FILL_HORIZONTAL)
      berksBoolean = new Button(container, SWT.BORDER | SWT.CHECK)
      berksBoolean.setLayoutData(gd)

      label = new Label(container, SWT.NULL)
      gd = new GridData(GridData.FILL_HORIZONTAL)
      label = new Label(container, SWT.NULL)
      label.setText("Use &DeliverFile:")
      gd = new GridData(GridData.FILL_HORIZONTAL)
      deliverFileBoolean = new Button(container, SWT.BORDER | SWT.CHECK)
      deliverFileBoolean.setLayoutData(gd)

      label = new Label(container, SWT.NULL)
      gd = new GridData(GridData.FILL_HORIZONTAL)
      label = new Label(container, SWT.NULL)
      label.setText("&License:")
      gd = new GridData(GridData.FILL_HORIZONTAL)
      licenseCombo = new Combo(container, SWT.BORDER | SWT.DROP_DOWN)
      licenseCombo.setLayoutData(gd)
      licenseCombo.addModifyListener(new ModifyListener() {
        def modifyText(e: ModifyEvent) {
          dialogChanged()
        }
      })
      LicenseType.values.foreach { value => licenseCombo.add(value.toString) }

      initialize()
      dialogChanged()
      setControl(container)
    }

    private def initialize() = {
      if (selection != null && selection.isEmpty == false && selection.isInstanceOf[IStructuredSelection]) {
        val ssel = selection.asInstanceOf[IStructuredSelection]
        if (ssel.size <= 1) {
          val obj = ssel.getFirstElement
          if (obj.isInstanceOf[IResource]) {
            var container: IContainer = null
            container = if (obj.isInstanceOf[IContainer]) obj.asInstanceOf[IContainer] else obj.asInstanceOf[IResource].getParent
          }
        }
      }
    }

    private def dialogChanged() = {
      //TODO validate here
      //    val name = getMaintainerEmailText
      //    if (name.length == 0) {
      //      updateStatus("Project name must be specified")
      //    } else {
      //      updateStatus(null)
      //    }
    }

    private def updateStatus(message: String) = {
      setErrorMessage(message)
      setPageComplete(message == null)
    }

    import LicenseType._

    def cookbookInfo = {
      CookbookInfo(berks = berksBoolean.getSelection,
        copyright = copyrightText.getText,
        deliveryFile = deliverFileBoolean.getSelection,
        email = maintainerEmailText.getText,
        license = LicenseType.values.find(_.toString == licenseCombo.getText).getOrElse(LicenseType.all_rights))
    }
  }

  private var selection: ISelection = _
  private var configElement: IConfigurationElement = _

  val page1: WizardNewProjectCreationPage = new WizardNewProjectCreationPage("Chef Cookbook Project Wizard")
  val page2: CookbookInfoWizardPage = new CookbookInfoWizardPage(selection)

  setNeedsProgressMonitor(true)

  override def addPages() {
    addPage(page1)
    addPage(page2)
  }

  override def setInitializationData(config: IConfigurationElement, propertyName: String, data: Object) = {
    configElement = config
  }

  override def performFinish(): Boolean = {
    val location = if (page1.useDefaults()) page1.getLocationURI else null
    def operation = ChefUtil.createCookbook(page1.getProjectName, location, page2.cookbookInfo)
    try {
      getContainer.run(true, false, operation)
    } catch {
      case e: InterruptedException => return false
      case e: InvocationTargetException => {
        val realException = e.getTargetException
        MessageDialog.openError(getShell, "Error", realException.getMessage)
        return false
      }
    }

    BasicNewProjectResourceWizard.updatePerspective(configElement)
    true
  }

  private def openContentStream(): InputStream = {
    val contents = "This is the initial file contents for *.mpe file that should be word-sorted in the Preview page of the multi-page editor"
    new ByteArrayInputStream(contents.getBytes)
  }

  private def throwCoreException(message: String) {
    val status = new Status(IStatus.ERROR, "chef-eclipse-plugin", IStatus.OK, message, null)
    throw new CoreException(status)
  }

  def init(workbench: IWorkbench, selection: IStructuredSelection) {
    this.selection = selection
  }
}
