package chefeclipseplugin.wizards

import org.eclipse.jface.viewers.ISelection
import org.eclipse.ui.IWorkbench
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.ui.INewWizard
import org.eclipse.jface.wizard.Wizard
import org.eclipse.jface.wizard.WizardPage
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.SWT
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard
import java.lang.reflect.InvocationTargetException
import org.eclipse.jface.dialogs.MessageDialog
import org.eclipse.core.runtime.IConfigurationElement
import org.eclipse.swt.widgets.Combo
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.widgets.Text
import org.eclipse.swt.events.ModifyEvent
import org.eclipse.swt.events.ModifyListener
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Label
import java.net.URI
import org.eclipse.core.resources.ResourcesPlugin
import chefeclipseplugin.nature.ChefNature
import org.eclipse.core.runtime.IExecutableExtension

abstract class ChefNewWizard extends Wizard() with INewWizard {
  protected var selection: ISelection = _

  setNeedsProgressMonitor(false)
  override def init(workbench: IWorkbench, selection: IStructuredSelection) {
    this.selection = selection
  }

  abstract class ResourcePage(private var selection: ISelection, resourceName: String) extends WizardPage("wizardPage") {
    protected def dialogChanged()

    protected var projectCombo: Combo = _
    protected var nameText: Text = _

    class Label(text: String, parent: Composite, style: Int = SWT.NULL)  {
      val label = new org.eclipse.swt.widgets.Label(parent, style)
      label.setText(text)
    }
    implicit def mine2Swt(label: Label): org.eclipse.swt.widgets.Label = label.label 

    protected def updateStatus(message: String) = {
      setErrorMessage(message)
      setPageComplete(message == null)
    }
    def selectedProject = ResourcesPlugin.getWorkspace.getRoot.getProject(projectCombo.getText)
    def addProjectCombo(container: Composite) = {
      val label = new Label("&Project", container)
      projectCombo = new Combo(container, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY)
      projectCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL))
      projectCombo.addModifyListener(new ModifyListener() {
        def modifyText(e: ModifyEvent) {
          dialogChanged()
        }
      })
      ResourcesPlugin.getWorkspace.getRoot.getProjects.filter(_.hasNature(ChefNature.NATURE_ID)).foreach(iProject =>
        projectCombo.add(iProject.getName))
    }
    def addNameText(container: Composite) = {
      val label = new Label(s"&$resourceName:", container)
      nameText = new Text(container, SWT.BORDER | SWT.SINGLE)
      nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL))
      nameText.addModifyListener(new ModifyListener() {
        def modifyText(e: ModifyEvent) {
          dialogChanged()
        }
      })

    }
  }

}

class RecipeNewWizard extends ChefNewWizard {
  class Page1(private var selection: ISelection) extends ResourcePage(selection, "Recipe") {
    private var maintainerEmailText: Text = _
    private var copyrightText: Text = _
    private var licenseCombo: Combo = _

    setTitle("New Recipe")
    setDescription("This wizard creates a new chef recipe.")

    override def createControl(parent: Composite) = {
      val container = new Composite(parent, SWT.NULL)
      val layout = new GridLayout()
      container.setLayout(layout)
      layout.numColumns = 2
      layout.verticalSpacing = 9

      addProjectCombo(container)
      addNameText(container)

      var label = new Label("&Maintainer Email Text:", container)
      maintainerEmailText = new Text(container, SWT.BORDER | SWT.SINGLE)
      maintainerEmailText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL))
      maintainerEmailText.addModifyListener(new ModifyListener() {
        def modifyText(e: ModifyEvent) {
          dialogChanged()
        }
      })

      label = new Label("&Copyright Text:", container)
      copyrightText = new Text(container, SWT.BORDER | SWT.SINGLE)
      copyrightText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL))
      copyrightText.addModifyListener(new ModifyListener() {
        def modifyText(e: ModifyEvent) {
          dialogChanged()
        }
      })

      label = new Label("&License:", container)
      licenseCombo = new Combo(container, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY)
      licenseCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL))
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
    }

    override def dialogChanged() = {
      val name = nameText.getText
      if (name.length == 0) {
        updateStatus("Recipe name must be specified")
      } else if (projectCombo.getSelectionIndex < 0) {
        updateStatus("Project must be specified")
      } else {
        updateStatus(null)
      }
    }
    import LicenseType._

    def recipeInfo = {
      RecipeInfo(name = nameText.getText,
        copyright = copyrightText.getText,
        email = maintainerEmailText.getText,
        license = LicenseType.values.find(_.toString == licenseCombo.getText).getOrElse(LicenseType.all_rights))
    }
  }
  val page1: Page1 = new Page1(selection)

  override def addPages() {
    addPage(page1)
  }

  override def performFinish(): Boolean = {
    val location: URI = null
    def operation = ChefUtil.createRecipe(page1.selectedProject, page1.recipeInfo)
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

    true
  }
}

class AttributeNewWizard extends ChefNewWizard {
  class Page1(private var selection: ISelection) extends ResourcePage(selection, "Attribute") {
    private var maintainerEmailText: Text = _
    private var copyrightText: Text = _
    private var licenseCombo: Combo = _

    setTitle("New Attribute")
    setDescription("This wizard creates a new chef attribute file.")

    override def createControl(parent: Composite) = {
      val container = new Composite(parent, SWT.NULL)
      val layout = new GridLayout()
      container.setLayout(layout)
      layout.numColumns = 2
      layout.verticalSpacing = 9

      addProjectCombo(container)
      addNameText(container)

      initialize()
      dialogChanged()
      setControl(container)
    }

    private def initialize() = {
    }
    protected override def dialogChanged() = {
      val name = nameText.getText
      if (name.length == 0) {
        updateStatus("Attribute name must be specified")
      } else if (projectCombo.getSelectionIndex < 0) {
        updateStatus("Project must be specified")
      } else {
        updateStatus(null)
      }
    }
    import LicenseType._

    def attributeInfo = AttributeInfo(name = nameText.getText)

  }
  val page1: Page1 = new Page1(selection)

  override def addPages() {
    addPage(page1)
  }

  override def performFinish(): Boolean = {
    val location: URI = null
    def operation = ChefUtil.createAttribute(page1.selectedProject, page1.attributeInfo)
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

    true
  }
}

class TemplateNewWizard extends ChefNewWizard {
  class Page1(private var selection: ISelection) extends ResourcePage(selection, "Template") {
    private var maintainerEmailText: Text = _
    private var copyrightText: Text = _
    private var licenseCombo: Combo = _

    setTitle("New Template")
    setDescription("This wizard creates a new chef template.")

    override def createControl(parent: Composite) = {
      val container = new Composite(parent, SWT.NULL)
      val layout = new GridLayout()
      container.setLayout(layout)
      layout.numColumns = 2
      layout.verticalSpacing = 9

      addProjectCombo(container)
      addNameText(container)

      initialize()
      dialogChanged()
      setControl(container)
    }

    private def initialize() = {
    }
    protected override def dialogChanged() = {
      val name = nameText.getText
      if (name.length == 0) {
        updateStatus("Template name must be specified")
      } else if (projectCombo.getSelectionIndex < 0) {
        updateStatus("Project must be specified")
      } else {
        updateStatus(null)
      }
    }
    import LicenseType._

    def templateInfo = TemplateInfo(name = nameText.getText)

  }
  val page1: Page1 = new Page1(selection)

  override def addPages() {
    addPage(page1)
  }

  override def performFinish(): Boolean = {
    val location: URI = null
    def operation = ChefUtil.createTemplate(page1.selectedProject, page1.templateInfo)
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

    true
  }
}

class FileNewWizard extends ChefNewWizard {
  class Page1(private var selection: ISelection) extends ResourcePage(selection, "File") {
    private var maintainerEmailText: Text = _
    private var copyrightText: Text = _
    private var licenseCombo: Combo = _

    setTitle("New Chef File")
    setDescription("This wizard creates a new chef file.")

    override def createControl(parent: Composite) = {
      val container = new Composite(parent, SWT.NULL)
      val layout = new GridLayout()
      container.setLayout(layout)
      layout.numColumns = 2
      layout.verticalSpacing = 9

      addProjectCombo(container)
      addNameText(container)

      initialize()
      dialogChanged()
      setControl(container)
    }

    private def initialize() = {
    }
    protected override def dialogChanged() = {
      val name = nameText.getText
      if (name.length == 0) {
        updateStatus("File name must be specified")
      } else if (projectCombo.getSelectionIndex < 0) {
        updateStatus("Project must be specified")
      } else {
        updateStatus(null)
      }
    }
    import LicenseType._

    def fileInfo = {
      FileInfo(name = nameText.getText)
    }
  }
  val page1: Page1 = new Page1(selection)

  override def addPages() {
    addPage(page1)
  }

  override def performFinish(): Boolean = {
    val location: URI = null
    def operation = ChefUtil.createFile(page1.selectedProject, page1.fileInfo)
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

    true
  }
}

class LWRPNewWizard extends ChefNewWizard {
  class Page1(private var selection: ISelection) extends ResourcePage(selection, "LWRP") {
    private var maintainerEmailText: Text = _
    private var copyrightText: Text = _
    private var licenseCombo: Combo = _

    setTitle("New LWRP")
    setDescription("This wizard creates a new chef lwrp.")

    override def createControl(parent: Composite) = {
      val container = new Composite(parent, SWT.NULL)
      val layout = new GridLayout()
      container.setLayout(layout)
      layout.numColumns = 2
      layout.verticalSpacing = 9

      addProjectCombo(container)
      addNameText(container)

      var label = new Label("&Maintainer Email Text:", container)
      maintainerEmailText = new Text(container, SWT.BORDER | SWT.SINGLE)
      maintainerEmailText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL))
      maintainerEmailText.addModifyListener(new ModifyListener() {
        def modifyText(e: ModifyEvent) {
          dialogChanged()
        }
      })

      label = new Label("&Copyright Text:", container)
      copyrightText = new Text(container, SWT.BORDER | SWT.SINGLE)
      copyrightText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL))
      copyrightText.addModifyListener(new ModifyListener() {
        def modifyText(e: ModifyEvent) {
          dialogChanged()
        }
      })

      label = new Label("&License:", container)
      licenseCombo = new Combo(container, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY)
      licenseCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL))
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
    }
    protected override def dialogChanged() = {
      val name = nameText.getText
      if (name.length == 0) {
        updateStatus("LWRP name must be specified")
      } else if (projectCombo.getSelectionIndex < 0) {
        updateStatus("Project must be specified")
      } else {
        updateStatus(null)
      }
    }
    import LicenseType._

    def lwrpInfo = {
      LwrpInfo(name = nameText.getText,
        copyright = copyrightText.getText,
        email = maintainerEmailText.getText,
        license = LicenseType.values.find(_.toString == licenseCombo.getText).getOrElse(LicenseType.all_rights))
    }
  }
  val page1: Page1 = new Page1(selection)

  override def addPages() {
    addPage(page1)
  }

  override def performFinish(): Boolean = {
    val location: URI = null
    def operation = ChefUtil.createLwrp(page1.selectedProject, page1.lwrpInfo)
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

    true
  }
}

