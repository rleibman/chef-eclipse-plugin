package chefeclipseplugin.nature;

import org.eclipse.core.resources.IProject
import org.eclipse.core.resources.IProjectNature
import org.eclipse.core.runtime.CoreException

object ChefNature {
  val NATURE_ID = "chef-eclipse-plugin.nature" 
}

class ChefNature extends IProjectNature {

  override def configure() = {
    // TODO Auto-generated method stub

  }

  override def deconfigure() = {
    // TODO Auto-generated method stub

  }

  override def getProject(): IProject = {
    // TODO Auto-generated method stub
    null;
  }

  override def setProject(project: IProject) = {
    // TODO Auto-generated method stub

  }

}
