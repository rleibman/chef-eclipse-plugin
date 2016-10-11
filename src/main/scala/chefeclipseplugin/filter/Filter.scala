package chefeclipseplugin.filter

import org.eclipse.jface.viewers.Viewer
import org.eclipse.jface.viewers.ViewerFilter

class Filter extends ViewerFilter {
	override def select(viewer: Viewer , parentElement: Object , element: Object): Boolean = {
	  println("=========================================================================================")
	  println(viewer.getClass)
	  println(parentElement.getClass)
	  println(element.getClass)
	  println("=========================================================================================")
		// TODO Auto-generated method stub
		true
	}

}
