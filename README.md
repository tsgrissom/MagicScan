# MagicScan

Scans configurations for potential issues and provides deeper analytics for [NathanWolf's Magic plugin](https://github.com/elBukkit/MagicPlugin) to ensure you have the cleanest, working-as-intended setup as possible.

## Commands

To do

## Permissions

To do

## Configuration

There are several configuration options available for MagicScan. See [here](https://github.com/PotterPlus/MagicScan/blob/master/src/main/resources/config.yml) for the default config.yml with examples.

Each rule can be configured, enabled/disabled, etc. from the rules.yml

| Key                                       | Type        | Description
|-------------------------------------------|-------------|------------------------
| scan_on_start                             | boolean     | Whether or not the plugin should initiate a scan when the server starts.
| scan_hidden                               | boolean     | Whether or not the plugin should scan hidden things.
| verbose                                   | boolean     | Whether or not the plugin should log loading messages.
| inactive_scan_timeout                     | integer     | How long after creating a scan, in ticks, it should automatically be removed.
| interval                                  | integer     | How many ticks to wait between displaying text elements.
| default_rule_types                        | string list | Which [RuleTypes](https://github.com/PotterPlus/MagicScan/blob/master/src/main/java/me/tylergrissom/magicscan/rule/RuleType.java) should be scanned by default.

### Messages

A lot of MagicScan messages are configurable via the [messages.yml](https://github.com/PotterPlus/MagicScan/blob/master/src/main/resources/config.yml).

## Rules

### Spell

| Key                                | Class                                                                                                                                                                                 | Description
|------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------
| actions                            | [SpellActionsRule](https://github.com/PotterPlus/MagicScan/blob/master/src/main/java/me/tylergrissom/magicscan/rule/spell/SpellActionsRule.java)                                        | Checks if the spell has any parameters that cannot be matched to an action it uses.
| category                           | [SpellCategoryRule](https://github.com/PotterPlus/MagicScan/blob/master/src/main/java/me/tylergrissom/magicscan/rule/spell/SpellCategoryRule.java)                                      | Checks if the spell has a valid spell category.
| cooldown                           | [SpellCooldownRule](https://github.com/PotterPlus/MagicScan/blob/master/src/main/java/me/tylergrissom/magicscan/rule/spell/SpellCooldownRule.java)                                      | Checks if the spell has a cooldown greater than zero.
| description                        | [SpellDescriptionRule](https://github.com/PotterPlus/MagicScan/blob/master/src/main/java/me/tylergrissom/magicscan/rule/spell/SpellDescriptionRule.java)                                | Checks if the spell has a description.
| icon                               | [SpellIconRule](https://github.com/PotterPlus/MagicScan/blob/master/src/main/java/me/tylergrissom/magicscan/rule/spell/SpellIconRule.java)                                              | Checks if the spell has a primary icon.
| icon_disabled                      | [SpellIconDisabledRule](https://github.com/PotterPlus/MagicScan/blob/master/src/main/java/me/tylergrissom/magicscan/rule/spell/SpellIconDisabledRule.java)                              | Checks if the spell has a disabled icon.
| key                                | [SpellKeyRule](https://github.com/PotterPlus/MagicScan/blob/master/src/main/java/me/tylergrissom/magicscan/rule/spell/SpellLevelsRule.java)                                             | Checks if the spell has an underscore, caps, or numbers besides denoting levels.
| levels                             | [SpellLevelsRule](https://github.com/PotterPlus/MagicScan/blob/master/src/main/java/me/tylergrissom/magicscan/rule/spell/SpellLevelsRule.java)                                          | Checks if the spell has leveling/progression.
| mana_match_path                    | [SpellManaMatchPathRule](https://github.com/PotterPlus/MagicScan/blob/master/src/main/java/me/tylergrissom/magicscan/rule/spell/SpellManaRule.java)                                     | Checks if the spell has casting/active mana costs.
| mana                               | [SpellManaRule](https://github.com/PotterPlus/MagicScan/blob/master/src/main/java/me/tylergrissom/magicscan/rule/spell/SpellManaRule.java)                                              | Checks if the spell has casting/active mana costs.
| name                               | [SpellNameRule](https://github.com/PotterPlus/MagicScan/blob/master/src/main/java/me/tylergrissom/magicscan/rule/spell/SpellNameRule.java)                                              | Checks if Magic's `messages.defaults.yml` file contains a `name` entry for the spell.
| path                               | [SpellPathRule](https://github.com/PotterPlus/MagicScan/blob/master/src/main/java/me/tylergrissom/magicscan/rule/spell/SpellPathRule.java)                                              | Checks if the spell is contained in a path as a regular, required, or extra spell.
| upgrade_description                | [SpellUpgradeDescriptionRule](https://github.com/PotterPlus/MagicScan/blob/master/src/main/java/me/tylergrissom/magicscan/rule/spell/SpellUpgradeDescriptionRule.java)                  | Checks if Magic's `messages.defaults.yml` file contains an `upgrade_description` entry for each leveled variant of a spell.

## Tags

MagicScan recognizes a couple of tags that can be attached to spells to manipulate or bypass rule behaviour on a per-spell basis. For example:

_In your spells.yml_

```yaml
spellkey:
  tags: noprogression
```

The above will not flag the [SpellLevelsRule](https://github.com/PotterPlus/MagicScan/blob/master/src/main/java/me/tylergrissom/magicscan/rule/spell/SpellLevelsRule.java) despite not having a second level.

The other tag offered so far is `default`. It is intended for spells that don't appear in a path (like those that appear on base wands by default) so that they can bypass failing a [SpellPathRule](https://github.com/PotterPlus/MagicScan/blob/master/src/main/java/me/tylergrissom/magicscan/rule/spell/SpellPathRule.java) check.
