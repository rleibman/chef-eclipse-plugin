package chefeclipseplugin.navigator

import org.eclipse.jface.viewers.ITreeContentProvider
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.core.resources.IProject
import org.eclipse.swt.graphics.Image
import chef_eclipse_plugin.Activator
import scala.beans.BeanProperty

class ContentProvider extends ITreeContentProvider {
  private lazy val chefProjectParents: Array[ChefProjectParent] = ResourcesPlugin.getWorkspace.getRoot.getProjects.map(ChefProjectParent(_))

  override def getElements(inputElement: Any): Array[Object] = {
    getChildren(inputElement)
  }

  override def getChildren(parentElement: Any): Array[Object] = {
    parentElement match {
      case parent: ChefNavigatorWorkbenchRoot =>
        chefProjectParents.asInstanceOf[Array[Object]]
      case element: IChefProjectElement => element.children.asInstanceOf[Array[Object]]
      case _ =>
        Array()
    }
  }

  override def getParent(element: Any): Object = {
    element match {
      case element: IChefProjectElement => element.parent
      case _ => null
    }
  }

  override def hasChildren(element: Object): Boolean = {
    element match {
      case element: IChefProjectElement => element.children.nonEmpty
      case element: ChefNavigatorWorkbenchRoot => chefProjectParents.nonEmpty
      case _ => false
    }
  }

}
