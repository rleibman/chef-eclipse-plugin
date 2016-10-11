package chefeclipseplugin.navigator

import org.eclipse.core.resources.IProject
import org.eclipse.swt.graphics.Image
import scala.beans.BeanProperty
import chef_eclipse_plugin.Activator

trait IChefProjectElement {
  def image: Image
  def children: Array[IChefProjectElement]
  def text: String
  def project: IProject
  def parent: Object
}

case class ChefProjectParent(project: IProject) extends IChefProjectElement {
  //TODO no elements here
  override lazy val children = Array[IChefProjectElement](
    AttributeFolder(this),
    RecipeFolder(this),
    FileFolder(this),
    TemplateFolder(this),
    LibraryFolder(this),
    TestFolder(this),
    LwrpFolder(this))
  override lazy val image = Activator.getImage("icons/chef_logo_v2_16x16.png")
  override def text: String = project.getName
  override def parent: Object = null
}

case class RecipeFolder(parent: IChefProjectElement) extends IChefProjectElement {
  override def children = {
    parent.project.getFolder("recipes").members().map(resource =>
      RubyFile(resource.getName, this))
  }
  override lazy val image = Activator.getImage("icons/chef_logo_v2_16x16.png")
  override val project = parent.project
  override val text = "Recipes"
}
case class TemplateFolder(parent: IChefProjectElement) extends IChefProjectElement {
  override def children = {
    parent.project.getFolder("templates").members().map(resource =>
      RubyFile(resource.getName, this))
  }
  override lazy val image = Activator.getImage("icons/chef_logo_v2_16x16.png")
  override val project = parent.project
  override val text = "Templates"
}
case class FileFolder(parent: IChefProjectElement) extends IChefProjectElement {
  override def children = {
    parent.project.getFolder("files").members().map(resource =>
      RubyFile(resource.getName, this))
  }
  override lazy val image = Activator.getImage("icons/chef_logo_v2_16x16.png")
  override val project = parent.project
  override val text = "Files"
}
case class AttributeFolder(parent: IChefProjectElement) extends IChefProjectElement {
  override def children = {
    parent.project.getFolder("attributes").members().map(resource =>
      RubyFile(resource.getName, this))
  }
  override lazy val image = Activator.getImage("icons/chef_logo_v2_16x16.png")
  override val project = parent.project
  override val text = "Attributes"
}
case class LwrpFolder(parent: IChefProjectElement) extends IChefProjectElement {
  override def children = {
    parent.project.getFolder("resources").members().map(resource =>
      RubyFile(resource.getName, this))
  }
  override lazy val image = Activator.getImage("icons/chef_logo_v2_16x16.png")
  override val project = parent.project
  override val text = "Lightweight Resources and Providers (LWRP)"
}
case class LibraryFolder(parent: IChefProjectElement) extends IChefProjectElement {
  override def children = {
    parent.project.getFolder("library").members().map(resource =>
      RubyFile(resource.getName, this))
  }
  override lazy val image = Activator.getImage("icons/chef_logo_v2_16x16.png")
  override val project = parent.project
  override val text = "Libraries"
}
case class TestFolder(parent: IChefProjectElement) extends IChefProjectElement {
  override def children = {
    parent.project.getFolder("test").members().map(resource =>
      RubyFile(resource.getName, this))
  }
  override lazy val image = Activator.getImage("icons/chef_logo_v2_16x16.png")
  override val project = parent.project
  override val text = "Tests"
}
case class RubyFile(text: String, parent: IChefProjectElement) extends IChefProjectElement {
  override lazy val children = Array[IChefProjectElement]()
  override lazy val image = Activator.getImage("icons/chef_logo_v2_16x16.png")
  override val project = parent.project
}
case class OtherFile(parent: IChefProjectElement) extends IChefProjectElement {
  override lazy val children = Array[IChefProjectElement]()
  override lazy val image = Activator.getImage("icons/chef_logo_v2_16x16.png")
  override val project = parent.project
  override val text = "other file"
}
case class TemplateFile(parent: IChefProjectElement) extends IChefProjectElement {
  override lazy val children = Array[IChefProjectElement]()
  override lazy val image = Activator.getImage("icons/chef_logo_v2_16x16.png")
  override val project = parent.project
  override val text = "template"
}