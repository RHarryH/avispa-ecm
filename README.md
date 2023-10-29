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

Each document or configuration which is stored in the database has a _type_ assigned to it. This type has to be registered
in special `TYPE` database table. This type contains type name (reused `OBJECT_NAME` column) and Java class name which
corresponds to the registered type. Class name has to be a unique value. The type can be persisted in the database in form
of an _objects_. Avispa ECM considers types name as case-insensitive.

### Objects hierarchy

The root of all objects is `ECM_ENTITY` table. It later divides to `ECM_CONFIG` and `ECM_OBJECT`. Latter one is designated for
objects related to [zip configuration](#zip-configuration) while former is a base for all other types.

None of the three mentioned tables are not registered in `TYPE` table what means they are not designated to exist as
independent objects. An actual type is represented by `DOCUMENT` table. Documents inherits from ECM objects and can be used
as independent objects within the ECM solution.

Full object metadata is a join of all tables tracked down to `ECM_ENTITY` table. For example for Document type, its metadata will
be in `DOCUMENT`, `ECM_OBJECT` and `ECM_ENTITY` tables identified by common UUID.

### Objects contents

Any object can have a _content_ file assigned to it. The file is stored in a _repository_, which is a special
folder in the OS configured in the properties file. Details about object content are stored in the `CONTENT` table and
are represented by `Content` type.

### Discriminators

Discriminators are a special columns marked on entity definition by `@TypeDiscriminator` annotation. They can serve as
a column allowing to distinct between some categories of subtypes, which are not "physical" types registered in `TYPE` table.
For example, we'd like to distinct between retail or customer client, but we want to store the data of both within
single database table.

### Rendition generating

Rendition is the source document converted to different format (for instance `.docx` to `.odt`).
In this ECM context it is always conversion from any format supported by `JODConverter` library to PDF.
It requires LibreOffice instance installed to properly performs the conversion as `JODConverter` is only a proxy
for LibreOffice CLI environment. In the future this solution might be externalized as separate Rendition Service.

## Zip configuration

Avispa ECM is designed for easy customization for specific needs. It has 3 levels of application configuration:

1. SQL scripts. Used to initialize ECM database by creating schema and inserting configurations entries for basic
   functionality.
2. `ecm-applicatin.properties` used for configuration of peripherals like file store or LibreOffice location.
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

All documents in ECM are of `Document` type. It is possible to extend it to create more specific types with additional
properties.
These types and their properties can be used to link with different properties. It is done using **Context**
configuration
item, which defines kind of a configuration matrix telling, for which kind of documents configuration items should be
triggered.
This enables for example using different naming conventions or templates for different documents types. Documents
applicable
for a context are defined as a *match rule* using Conditions.

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

## Expressions processing

Expressions are simple pseudo-scripts allowing to build custom strings using objects properties. For
example, it is used to define name of the documents using autonaming or folder name for autolinking.

### Syntax

#### Constants

Text in single quotes represents a constant string: `'This is string'`

#### Operators

Currently, only concatenation operator is supported, and it is represented by plus symbol `+`. It is used
to concatenate constants and functions results into single string. Example:
`'Concatenation' + 'Test'` will result in `Concatenation Test` string.

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