package chefeclipseplugin.wizards

/*
   * -g GENERATOR_COOKBOOK_PATH, --generator-cookbook GENERATOR_COOKBOOK_PATH
   * The path at which a cookbook named code_generator is located. This cookbook is used by the chef generate subcommands to generate cookbooks, cookbook files, templates, attribute files, and so on. Default value: lib/chef-dk/skeletons, under which is the default code_generator cookbook that is included as part of the Chef development kit.
   * -b, --berks Create a Berksfile in the cookbook. Default value: enabled. This is disabled if the --policy option is given.
   * -C COPYRIGHT, --copyright COPYRIGHT Specify the copyright holder for copyright notices in generated files. Default value: The Authors
   * -d, --delivery Generate a delivery config file and build cookbook inside the new cookbook. Default value: disabled.
   * -m EMAIL, --email EMAIL Specify the email address of the author. Default value: you@example.com.
   * -a KEY=VALUE, --generator-arg KEY=VALUE
   * Sets a property named KEY to the given VALUE on the generator context object in the generator cookbook. This allows custom generator cookbooks to accept optional user input on the command line.
   * -I LICENSE, --license LICENSE
   * Sets the license. Valid values are all_rights, apache2, mit, gplv2, or gplv3. Default value: all_rights.   *
*/
object LicenseType extends Enumeration {
  type LicenseType = Value
  val all_rights, apache2, mit, gplv2, gplv3 = Value
}

import LicenseType._

case class CookbookInfo(
  berks: Boolean = true,
  copyright: String = "The Authors",
  deliveryFile: Boolean = false,
  email: String = "you@example.com",
  license: LicenseType = LicenseType.all_rights)

/**
 *
 * -C, --copyright COPYRIGHT        Name of the copyright holder - defaults to 'The Authors'
 * -m, --email EMAIL                Email address of the author - defaults to 'you@example.com'
 * -a, --generator-arg KEY=VALUE    Use to set arbitrary attribute KEY to VALUE in the code_generator cookbook
 * -h, --help                       Show this message
 * -I, --license LICENSE            all_rights, apachev2, mit, gplv2, gplv3 - defaults to all_rights
 * -v, --version                    Show chef version
 * -g GENERATOR_COOKBOOK_PATH,      Use GENERATOR_COOKBOOK_PATH for the code_generator cookbook
 * --generator-cookbook
 *
 */
case class RecipeInfo(
  name: String,
  copyright: String = "The Authors",
  email: String = "you@example.com",
  license: LicenseType = LicenseType.all_rights)

/**
 * Usage: chef generate attribute [path/to/cookbook] NAME [options]
 * -C, --copyright COPYRIGHT        Name of the copyright holder - defaults to 'The Authors'
 * -m, --email EMAIL                Email address of the author - defaults to 'you@example.com'
 * -a, --generator-arg KEY=VALUE    Use to set arbitrary attribute KEY to VALUE in the code_generator cookbook
 * -h, --help                       Show this message
 * -I, --license LICENSE            all_rights, apachev2, mit, gplv2, gplv3 - defaults to all_rights
 * -v, --version                    Show chef version
 * -g GENERATOR_COOKBOOK_PATH,      Use GENERATOR_COOKBOOK_PATH for the code_generator cookbook
 * --generator-cookbook
 */
case class AttributeInfo(name: String)

/**
 * Usage: chef generate template [path/to/cookbook] NAME [options]
 * -C, --copyright COPYRIGHT        Name of the copyright holder - defaults to 'The Authors'
 * -m, --email EMAIL                Email address of the author - defaults to 'you@example.com'
 * -a, --generator-arg KEY=VALUE    Use to set arbitrary attribute KEY to VALUE in the code_generator cookbook
 * -h, --help                       Show this message
 * -I, --license LICENSE            all_rights, apachev2, mit, gplv2, gplv3 - defaults to all_rights
 * -s, --source SOURCE_FILE         Copy content from SOURCE_FILE
 * -v, --version                    Show chef version
 * -g GENERATOR_COOKBOOK_PATH,      Use GENERATOR_COOKBOOK_PATH for the code_generator cookbook
 * --generator-cookbook
 */
case class TemplateInfo(name: String)

/**
 * Usage: chef generate file [path/to/cookbook] NAME [options]
 * -C, --copyright COPYRIGHT        Name of the copyright holder - defaults to 'The Authors'
 * -m, --email EMAIL                Email address of the author - defaults to 'you@example.com'
 * -a, --generator-arg KEY=VALUE    Use to set arbitrary attribute KEY to VALUE in the code_generator cookbook
 * -h, --help                       Show this message
 * -I, --license LICENSE            all_rights, apachev2, mit, gplv2, gplv3 - defaults to all_rights
 * -s, --source SOURCE_FILE         Copy content from SOURCE_FILE
 * -v, --version                    Show chef version
 * -g GENERATOR_COOKBOOK_PATH,      Use GENERATOR_COOKBOOK_PATH for the code_generator cookbook
 * --generator-cookbook
 */
case class FileInfo(name: String)

/**
 * Usage: chef generate lwrp [path/to/cookbook] NAME [options]
 * -C, --copyright COPYRIGHT        Name of the copyright holder - defaults to 'The Authors'
 * -m, --email EMAIL                Email address of the author - defaults to 'you@example.com'
 * -a, --generator-arg KEY=VALUE    Use to set arbitrary attribute KEY to VALUE in the code_generator cookbook
 * -h, --help                       Show this message
 * -I, --license LICENSE            all_rights, apachev2, mit, gplv2, gplv3 - defaults to all_rights
 * -v, --version                    Show chef version
 * -g GENERATOR_COOKBOOK_PATH,      Use GENERATOR_COOKBOOK_PATH for the code_generator cookbook
 * --generator-cookbook
 */
case class LwrpInfo(name: String,
  copyright: String = "The Authors",
  email: String = "you@example.com",
  license: LicenseType = LicenseType.all_rights)



  