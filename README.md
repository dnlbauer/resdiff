# resdiff
A tool for comparing android resource files similar to unix diff

## Usage

```
danijoo > .\resdiff --help
usage: resdiff [OPTIONS]... FILES
 -h,--help           Show this screen
 -t,--type <arg>     only compare this resource type (string, bool,
                     integer, color, dimen)
 -y,--side-by-side   output in columns
```

## Examples
### Table view
```
danijoo > .\resdiff -y resourcefile1.xml resourcefile2.xml
Found 9 differences (1 added, 2 removed).
+-----------+--------------------------+-------------------------------+-----------------------------------+
|    Type   |           Name           |              Left             |               Right               |
+-----------+--------------------------+-------------------------------+-----------------------------------+
|    String |      not_matching_string | This resource is not matching | This resource is not matching!!!! |
|    String |       missing_string_res | This misses in the other file |                                   |
|    String |       res_with_reference |      @string/is_just_a_string |    @string/is_just_another_string |
|    String |         added_string_res |                               |     This misses in the other file |
|   Boolean |        not_matching_bool |                         false |                              true |
|   Boolean |        a_missing_boolean |                          true |                                   |
|   Integer |         not_matching_int |                           122 |                               533 |
| Dimension |       not_matching_dimen |                           1sp |                              99px |
|     Color |       not_matching_color |                       #001230 |                           #666666 |
+-----------+--------------------------+-------------------------------+-----------------------------------+
```

### Normal view
```
danijoo > .\resdiff resourcefile1.xml resourcefile2.xml
Found 9 differences (1 added, 2 removed).
<string name="not_matching_res">
<	This resource is not matching
---
>	This resource is not matching!!!!
<string name="missing_string_res">
<	This misses in the other file
<string name="a_reference_not_matching">
<	@string/is_just_a_string
---
>	@string/is_just_another_string

