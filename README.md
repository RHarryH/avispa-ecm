![Status](https://github.com/RHarryH/avispa-ecm/actions/workflows/main.yml/badge.svg) ![Coverage](.github/badges/jacoco.svg)  [![License: AGPL v3](https://img.shields.io/badge/License-AGPL%20v3-blue.svg)](https://www.gnu.org/licenses/agpl-3.0)

# Avispa ECM

A small framework for implementing basic ECM (Enterprise Content Management) solution. It is a second, more general and
highly inspired by OpenText Documentum ECM, iteration of previous (not published) solution for knowledge base
application.

It provides basic feature for storing and managing documents:

- custom property pages, which can be utilized in any UI application basing on the Avispa ECM
- using documents templates
- creation of logical folders structure within an ECM allowing to keep documents in an organized way without any further
  actions
- auto-generating of documents names
- possibility of extension and customization to fit the application needs
- multiple configurations for different document types using custom MongoDB-like query language for conditions resolving
- auto-generating (thanks to LibreOffice) of so called renditions - pdf variants of original documents

## Roadmap

As mentioned at the beginning, Avispa ECM provides support for the basic features only. The high-level roadmap for
extension looks like below

- basic authentication and authorization mechanisms (now the ECM is designed only for one-person use)
- localization
- documents versioning
- linking documents using relations (for example for including attachments)
- checking in and checking out documents for editing with autoversioning
- uploading files with external content rather than relying only on templates
- lifecycles
- workflows

## ECM specific properties

| Property name                        | Description                                                                                                                       |
|--------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| `avispa.ecm.name`                    | Name of the ECM solution                                                                                                          |
| `avispa.ecm.file-store.path`         | Path where the documents will be physically stored. By default it will be `default-file-store` folder in home directory on `dev`. |
| `avispa.ecm.file-store.name`         | Name of the file store in the database.                                                                                           |
| `avispa.ecm.configuration.paths`     | Comma separated paths to `.zip` configuration loaded during tests and `dev` setup.                                                |
| `avispa.ecm.configuration.overwrite` | `true` if the existing configuration should be overwritten                                                                        |
| `avispa.ecm.office.home`             | Home location to LibreOffice. `C:\Program Files\LibreOffice` by default.                                                          |

## Types and objects

Each document or configuration which is stored in the database has a _type_ assigned to it. This type has to be
registered in special `TYPE` database table. This type contains type name (reused `OBJECT_NAME` column) and Java class 
name which corresponds to the registered type. Class name has to be a unique value. The type can be persisted in the 
database in form of an _objects_. Avispa ECM considers types name as case-insensitive.

Types should be considered as of OOP classes counterparties. This means if custom type is implemented by extending other
existing type (for example `Invoice` being extension of `Document` by declaring class
as `class Invoice extends Document`),
the type will implicitly reflect that hierarchy, which might impact for example contexts match rules.
See more in [Context](#context) section.

### Objects hierarchy

The root of all objects is `ECM_ENTITY` table. It later divides to `ECM_CONFIG` and `ECM_OBJECT`. Latter one is
designated for objects related to [zip configuration](#zip-configuration) while former is a base for all other types.

None of the three mentioned tables are not registered in `TYPE` table what means they are not designated to exist as
independent objects. An actual type is represented by `DOCUMENT` table. Documents inherits from ECM objects and can be
used as independent objects within the ECM solution.

Full object metadata is a join of all tables tracked down to `ECM_ENTITY` table. For example for Document type, its 
metadata will be in `DOCUMENT`, `ECM_OBJECT` and `ECM_ENTITY` tables identified by common UUID.

### Objects contents

Any object can have a _content_ file assigned to it. The file is stored in a _repository_, which is a special
folder in the OS configured in the properties file. Details about object content are stored in the `CONTENT` table and
are represented by `Content` type.

### Discriminators

Discriminators are a special columns marked on entity definition by `@TypeDiscriminator` annotation. They can serve as
a column allowing to distinct between some categories of subtypes, which are not "physical" types registered in `TYPE`
table. For example, we'd like to distinct between retail or customer client, but we want to store the data of both 
within single database table.

### Rendition generating

Rendition is the source document converted to different format (for instance `.docx` to `.odt`).
In this ECM context it is always conversion from any format supported by `JODConverter` library to PDF.
It requires LibreOffice instance installed to properly performs the conversion as `JODConverter` is only a proxy
for LibreOffice CLI environment. In the future this solution might be externalized as separate Rendition Service.

## Zip configuration

Avispa ECM is designed for easy customization for specific needs. It has 3 levels of application configuration:

1. SQL scripts. Used to initialize ECM database by creating schema and inserting configurations entries for basic
   functionality.
2. `ecm.properties` used for configuration of peripherals like file store or LibreOffice location.
3. Zip file containing JSON-based specific configuration telling how specific objects should behave at certain
   conditions or how they should be managed.

The last level is designated for the end-users. It allows to configure following aspects of ECM:

- **Autolink** - rules telling to which logical folder the document should be linked after creation
- **Autoname** - rules telling what should be the name of the document withing the ECM
- **Dictionary** - dictionary is used in property pages for selecting only purposed values
- **Property page** - definition of insert or update form used by GUI applications (like ECM Application)
- **Template** - template document used by the customizations. It can be used for example to generate invoices,
  brochures or reports with fixed structure.
- **Upsert** - configuration item telling, which property page should be used when inserting or updating document

### Context

All documents in ECM are of `Document` type. It is possible to extend it to create more specific types with additional
properties. These types and their properties can be used to link with different properties. It is done using **Context**
configuration item, which defines kind of a configuration matrix telling, for which kind of documents configuration 
items should be triggered. This enables for example using different naming conventions or templates for different 
documents types. Documents applicable for a context are defined as a _match rule_ using Conditions.

It is important to think about types as hierarchical structures similar to classes in OOP. This means that any subtype
of `Document` will also be a `Document`. This impacts how contexts are resolved allowing to specify generic
configurations
for all documents or specific subtypes group. However, it is not recommended it is possible to create contexts for base
type and use subtypes properties in the match rule. The context might be then applied to all `Document` subtypes
containing that property and matching the specified value. This allows fine-grained context preparation but in most use
cases it might be too confusing causing seemingly unexpected behavior. To better understand this use case please see
below example.

**Types**: `Document` and `Invoice` whose implementation extends `Document` and contains additional `serialNumber`
property

**Context** (the configuration is stored in the database but for the simplicity it is presented as equivalent JSON):

```json
{
  "type": "Document",
  "matchRule": {
    "serialNumber": 10
  }
}
```

When retrieving matching configurations for `Invoice` object containing `serialNumber` property with `10` as a value,
above context configuration despite being defined for `Document` type, will match that object so all configuration
elements defined within that context will be applied to that object. This happens because `Invoice` is a subtype
of `Document` type.

### Dictionary

_Dictionaries_ are key-value maps for storing expected values for objects fields. They can be later used for example on
the UI as options for combo or radio boxes. The key is called a _label_ while value is a map of columns and their 
respective values. It means single dictionary value can keep multiple values in fact. For example if we want to use 
dictionary to store VAT rates the label will look like `VAT_08` and the value can contain `multiplier` column with 
`0.08` value and `description` column with some additional explanation about the purpose of the value.

Dictionary can be linked to the object field by annotating it with `@Dictionary` annotation and providing dictionary
name.

### Property page

Property page is used to define the layout of UI form for displaying object fields (known also as _properties_).
Property page configuration requires a JSON content file with the layout details. Object fields are wrapped in so-called
_property controls_. There are also different kind of _controls_ which are not related to properties
like `separator`, `label` or _grouping controls_ like `group`, `columns`, `table` or `tabs`. The file structure is
defined in JSON Schema files found [here](src/main/resources/json-schemas). For additional details on control-specific
properties please check
[this](#controls) section.

Below are some of the general details about grouping limitations:

- `columns` can have up to 4 nested controls, which are not a grouping controls.
- `table` can use only `checkbox`, `combo`, `date`, `datetime`, `money`, `number`, and `text` controls. Constraints are
  not allowed for these controls, and they are always required (except for checkboxes, they have always
  only `true/false`
  values). Tables cannot be present in any grouping control.
- `group` does not allow to nest another group within it. However `columns` or `tabs` are allowed.
- `tabs` allows to nest `columns` and `groups` without `tabs` nested.

#### Context mode

Property page can be run in one of multiple context modes:

- `READONLY` - property page can be used only to show object data
- `INSERT` - properties are editable but all `id` fields are hidden
- `EDIT` - properties are editable, `id`s of objects are passed to the property page

#### Accessibility constraints

Apart from controls defined in `table`, property controls can have properties, which tells whether they should be
readonly, required or visible. The basic ones are:

- `required` property tells if the value of property must be provided or is optional. For `checkbox` requirement means,
  the checkbox has to be checked.
- `readonly` property allows to make control always read only. Please note if property page is opened in `READONLY`
  context, this property is always overwritten to `true`

To have more fine-grained control over properties behavior they can be configured with `constraints` node. There are
three categories of constraints:

- `visibility` constraint tells whether the control should be hidden
- `requirement` constraint tells whether the control should be required and overwrites `required` property setting
- `modifiable` constraint tells whether the control should be modifiable and overwrites `readonly` property setting.
  Please note it has reversed meaning to the `readonly` - when resolved condition will return positive result, the
  property can be modified.

This applies to all controls apart from following exclusions:

- `table` nested properties and `columns` are not controllable in such way
- `group`, `tabs`, `label` and `separator` have only `visibility` constraint

Constraints can be defined in two ways:

- as [conditions](#conditions) (accessibility is determined based on the values of other properties)
- by defining property page [context mode](#context-mode) in which behavior should be applied.

#### Controls

Apart from common properties described in the previous sections, controls can have many properties specific to them.
Each control has also `type` property, which can take one of the values presented in the [main](#property-page) section
and describes the control type. Property controls have to define `property` control to tell, which value will be read
or write. For some controls (see `table`) it has additional meaning.

##### Table

| Property name | Type      | Required | Description                                                                                                                          |
|---------------|-----------|----------|--------------------------------------------------------------------------------------------------------------------------------------|
| `label`       | `string`  | No       | Label with the name of the table                                                                                                     |
| `fixed`       | `boolean` | No       | When set to yes, the possibility of adding or removing new rows should be forbidden (only modification of existing rows is possible) |
| `property`    | `string`  | Yes      | Root property name, maps to `java.util.List` field in the database entity                                                            |
| `controls`    | `array`   | Yes      | Array of controls for display root property properties. They are relative to the root `property`.                                    |

##### Combobox and radio button

Both `combobox` and `radio` controls have to load some static or dynamic dictionary defined through `loadSettings`
property. It can have one of two structures depending on the type of dictionary.

For static dictionaries:

| Property name | Type      | Required | Description                                                                                  |
|---------------|-----------|----------|----------------------------------------------------------------------------------------------|
| `dictionary`  | `string`  | Yes      | Name of the dictionary from the [zip configuration](#zip-configuration)                      |
| `sortByLabel` | `boolean` | No       | By default dictionary values are sorted by the values keys. This option allows to change it. |

For dynamic dictionaries:

| Property name   | Type     | Required | Description                                                                                                   |
|-----------------|----------|----------|---------------------------------------------------------------------------------------------------------------|
| `type`          | `string` | Yes      | Name of the type, which should be queried                                                                     |
| `qualification` | `string` | No       | Additional qualification to narrow the result. Qualification is a regular [condition](#conditions-processing) |

## Conditions processing

Conditions provide a way to define simple queries without the need to know languages like SQL.
It has also security benefits narrowing the possibilities only to narrow set of operations.
Conditions use JSON data interchange format specified in [RFC-8259](https://www.rfc-editor.org/rfc/rfc8259.html) and
MongoDB-like syntax described in `context-rule.json` JSON Schema.

Conditions are used for example in context match rules, but they can also be used on the frontend in
conditional controls visibility or requirement.

Please note that RFC document (chapter 4) specifies that the JSON keys within an object must be unique
otherwise their behavior is unpredictable but in many cases the latter value is used.

### Syntax

All conditions specified in the root of the JSON are grouped in the default `and` group. It means there is
no need to group conditions in explicit `and` group unless we want to nest it in `or` groups.

#### Conditions

In all cases we have to specify `propertyName` and `value` used in condition check. Property name
must be an alphanumeric string starting with letter only. It can contain dot `.` character to dereference
nested property like `payment.method`. Possible values are numbers (both integer or floating-point) for
all checks types. Additionally, for equity checks strings and boolean values can be provided.

##### Equity

For equity check we can use one out of two ways:

```json
{
  "propertyName": "value"
}
```

or

```json
{
  "propertyName": {
    "$eq": "value"
  }
}
```

To check if property does not equal certain value use `$ne` operator

```json
{
  "propertyName": {
    "$ne": "value"
  }
}
```

##### String search

Conditions support `like` and `not like` operators known from SQL. The escape character is set to `\ `. To apply this
operators use `$like` and `$notLike` respectively. Only strings can be used with this operators.

```json
{
  "propertyName": {
    "$like": "val%e"
  }
}
```

```json
{
  "propertyName": {
    "$notLike": "sampl_\\_text"
  }
}
```

##### Comparison

###### Greater than

```json
{
  "propertyName": {
    "$ge": 12
  }
}
```

###### Greater than or equal

```json
{
  "propertyName": {
    "$gte": 12
  }
}
```

###### Less than

```json
{
  "propertyName": {
    "$le": 12
  }
}
```

###### Less than or equal

```json
{
  "propertyName": {
    "$lte": 12
  }
}
```

#### Conditions grouping

Conditions can be grouped in `and` or `or` groups. Each condition has to be a separate object element in
at least 2 element array.

```json
{
  "$and": [
    {
      "propertyName": {
        "$lte": 12
      }
    },
    {
      "propertyName2": {
        "$eq": "string"
      }
    }
  ]
}
```

is equal to: `propertyName < 12 and propertyName2 = 'string'`

```json
{
  "$or": [
    {
      "propertyName": {
        "$lte": 12
      }
    },
    {
      "propertyName2": {
        "$eq": "string"
      }
    }
  ]
}
```

is equal to: `propertyName < 12 or propertyName2 = 'string'`

Groups can be nested within other groups.

#### Modifiers

Modifiers allows to modify conditions result. Currently, two modifiers are implemented: limiting the number of the
result and ordering the result.

##### Limiting

To limit number of rows returned use `$limit` modifier at the root level. Limiting number must be
greater than zero.

```json
{
  "propertyName": "value",
  "$limit": 10
}
```

##### Ordering

To order the result based on certain properties use `$orderBy` modifier. It is an object accepting an arbitrary number
of properties. Use property name as a key and a constant describing direction of ordering as a value. The available
constants are `asc` for ascending order and `desc` for descending order.

```json
{
  "propertyName": "value",
  "$orderBy": {
    "propertyName": "asc",
    "propertyName2": "desc"
  }
}
```

## Expressions processing

Expressions are simple pseudo-scripts allowing to build custom strings using objects properties. For
example, it is used to define name of the documents using autonaming or folder name for autolinking.

### Syntax

Expressions work in two context. The outer context contains any symbols that are not a function or are not withing the
function. Whenever the parser enters the function, it enters into inner, function context where all the rules below
apply. In the outer context you can type any characters you want, and they will remain unchanged after the parsing.

The only exception is a dollar sign followed by an alphanumeric set of characters and left parenthesis, because this is
interpreted as the header of the function. To use dollar sign before function-header-like string use backslash
before the dollar sign: `This is \$notAFunction(`.

Examples:

- `Regular text $value('Function context.' + 'concatenation is required') regular text`
- `Dollar $ is allowed except this: \$case`

#### Constants

Text in single quotes represents a constant string: `'This is string'`. To use apostrophe inside the text use backslash
as escape character: `'I\'m the string'`.

#### Operators

Currently, only concatenation operator is supported, and it is represented by plus symbol `+`. It is used
to concatenate constants and functions results into single string. Example:
`'Concatenation ' + 'Test'` will result in `Concatenation Test` string.

#### Functions

Functions use following syntax: `$<function_name>([param_1][,param_2]...)`. When there will be used non-existing
function then it will be treated as a text.

##### Value extraction

`$value(propertyName)` - extracts value from object property. Works with nested values.
Examples: `$value('objectName')` `$value('payment.bankAccount')`

##### Date value extraction

`$datevalue(propertyName, format)` - works like above but for properties of `LocalDate` or `LocalDateTime` type
returns value formatted according to the format in `format` parameter. The format used by `format` parameter is defined
in [Java documentation](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/time/format/DateTimeFormatter.html)
Example: `$datevalue('testDateTime', 'MM')`

##### Default value

`$default(value, defaultValue)` - when the value is an empty value then the default value will be used. Can be combined
with other functions. Examples: `$default($value('testString'), 'This is default value')`

##### Padding

`$pad(value, n[, paddingCharacter])` - pad left string to `n` characters in total using padding character or if not
provided - default `0` value.
If number of characters will not be a correct positive integer then original input will be returned.
Examples: `$pad('a', '4') => 000a` `$pad('a', '4', 'X') => XXXa`

## Antlr4 support

By default, Antlr4 grammar files (`.g4`) should be located in `/src/main/antlr4`
folder. In order to properly use generated files, this folder has to be marked as
`Sources Root`. You can double-check if everything is correct by opening `Module Settings` and checking
the paths for `Source Folders`.