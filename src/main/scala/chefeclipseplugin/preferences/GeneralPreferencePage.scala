package chefeclipseplugin.preferences

import org.eclipse.jface.preference.PreferencePage
import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Control
import org.eclipse.ui.IWorkbench
import org.eclipse.ui.IWorkbenchPreferencePage
import org.eclipse.swt.SWT
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.Label
import org.eclipse.swt.widgets.Text
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.widgets.Button
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.widgets.FileDialog
import org.eclipse.swt.events.SelectionListener
import org.eclipse.core.runtime.preferences.InstanceScope
import org.eclipse.jface.preference.FieldEditorPreferencePage
import chef_eclipse_plugin.Activator
import org.eclipse.jface.preference.FileFieldEditor

class GeneralPreferencePage extends FieldEditorPreferencePage(FieldEditorPreferencePage.GRID) with IWorkbenchPreferencePage {
  override def init(workbench: IWorkbench) = {
    val store = Activator.getDefault().getPreferenceStore()

    setPreferenceStore(store);
    setDescription("Chef properties");
  }
  override def createFieldEditors() = {
    addField(new FileFieldEditor("chefExecutablePath", "chef Executable", getFieldEditorParent()))
    addField(new FileFieldEditor("berksExecutablePath", "berks Executable", getFieldEditorParent()))
    addField(new FileFieldEditor("kitchenExecutablePath", "kitchen executable", getFieldEditorParent()))
    addField(new FileFieldEditor("rubocopExecutablePath", "rubocop executable", getFieldEditorParent()))
    addField(new FileFieldEditor("foodcriticExecutablePath", "foodcritic executable", getFieldEditorParent()))
  }
  //  val preferences = InstanceScope.INSTANCE.getNode("net.leibman.chef-eclipse-plugin")
  //  override def init(workbench: IWorkbench) {
  //
  //  }
  //
  //  class Label(text: String, parent: Composite, style: Int = SWT.NULL) {
  //    val label = new org.eclipse.swt.widgets.Label(parent, style)
  //    label.setText(text)
  //  }
  //  implicit def mine2Swt(label: Label): org.eclipse.swt.widgets.Label = label.label
  //  implicit def fn2SelectionListener(fn: SelectionEvent => Unit): SelectionListener = {
  //    new SelectionListener {
  //      def widgetDefaultSelected(event: SelectionEvent) = widgetSelected(event)
  //      def widgetSelected(event: SelectionEvent) = {
  //        fn(event)
  //      }
  //    }
  //
  //  }
  //
  //  protected override def createContents(parent: Composite): Control = {
  //    val container = new Composite(parent, SWT.NULL)
  //    val layout = new GridLayout()
  //    container.setLayout(layout)
  //    layout.numColumns = 2
  //    layout.verticalSpacing = 9
  //
  //    {
  //      val label = new Label("Chef Executable", container, SWT.NULL)
  //
  //      val together = new Composite(container, SWT.NULL)
  //      together.setLayout(layout)
  //      together.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false))
  //
  //      val chefExecutablePath = new Text(together, SWT.SINGLE | SWT.BORDER)
  //      chefExecutablePath.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false))
  //      chefExecutablePath.setText(preferences.get("chefExecutablePath", "/usr/bin/chef"))
  //
  //      val button = new Button(together, SWT.NONE);
  //      button.setText("...");
  //      button.addSelectionListener { event: SelectionEvent =>
  //        val dlg = new FileDialog(button.getShell(), SWT.OPEN)
  //        dlg.setText(label.getText)
  //        dlg.setFileName(chefExecutablePath.getText)
  //        val path = dlg.open()
  //        if (path != null)
  //          chefExecutablePath.setText(path)
  //      }
  //    }
  //
  //    container
  //  }
}
