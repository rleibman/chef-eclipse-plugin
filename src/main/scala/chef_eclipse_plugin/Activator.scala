package chef_eclipse_plugin

import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.ui.plugin.AbstractUIPlugin
import org.osgi.framework.BundleContext
//remove if not needed
import scala.collection.JavaConversions._

object Activator {

  val PLUGIN_ID = "chef-eclipse-plugin"

  private var plugin: Activator = _

  def getDefault(): Activator = plugin

  def getImageDescriptor(path: String): ImageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path)

  def getImage(imagePath: String) = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, imagePath).createImage()
}

class Activator extends AbstractUIPlugin {
  import Activator._

  override def start(context: BundleContext) {
    super.start(context)
    plugin = this
    val store = getPreferenceStore()
    store.setDefault("chefExecutablePath", "/usr/bin/chef")
    store.setDefault("berksExecutablePath", "/usr/bin/berks")
    store.setDefault("kitchenExecutablePath", "/usr/bin/kitchen")
    store.setDefault("rubocopExecutablePath", "/usr/bin/rubocop")
    store.setDefault("foodcriticExecutablePath", "/usr/bin/foodcritic")
  }

  override def stop(context: BundleContext) {
    plugin = null
    super.stop(context)
  }
}
