package chefeclipseplugin.navigator

import org.eclipse.jface.viewers.ILabelProvider
import org.eclipse.jface.viewers.ILabelProviderListener
import org.eclipse.swt.graphics.Image

class LabelProvider extends ILabelProvider {

  override def addListener(listener: ILabelProviderListener) = {
    //Empty on purpose
  }

  override def dispose() = {
    //Empty on purpose
  }

  override def isLabelProperty(element: Any, property: String): Boolean = {
    //Empty on purpose
    false
  }

  override def removeListener(listener: ILabelProviderListener) = {
    //Empty on purpose
  }

  override def getImage(element: Any): Image = {
    element match {
      case element: IChefProjectElement => element.image
      case _ => null
    }
  }

  override def getText(element: Any): String = {
    element match {
      case parent: ChefProjectParent => parent.project.getName
      case element: IChefProjectElement => element.text
      case _ => null
    }
  }

}
