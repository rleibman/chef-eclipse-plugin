package chefeclipseplugin.navigator

import org.eclipse.ui.navigator.CommonNavigator

case class ChefNavigatorWorkbenchRoot()

class ChefNavigator extends CommonNavigator {
  override protected def getInitialInput() = {
    ChefNavigatorWorkbenchRoot()
  }
}
