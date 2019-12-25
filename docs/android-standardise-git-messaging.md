# Standardise Git messages (commit and PR)

* Platform: Development
* Status: accepted
* Deciders: Tech leads on behalf of developers
* Date: 2019-05-13


## Context and Problem Statement

Right now, there is no standard way of naming peer reviews and the commit message standards are not reinforced.
This inconsistency creates confusion and makes it hard to follow Git history and pick out peer reviews. 

## Proposal

We keep the existing branch naming structure and start enforcing it on Github:

### Branch names
```
master
release/<version>
develop
feature/<TICKET-number>-short-description
bugfix/<TICKET-number>-short-description
hotifx/<TICKET-number>-short-description
merge/<version>-to-<version>_YYYY-MM-DDD

```

### Commit messages

We keep the existing commit message structure and enforce it on Github:
```
[TICKET-number][bugfix|feature|hotfix|refactor] Description of what changed in this commit.

More details of the changes, if required.
```

For merge commits:
```
[merge] Description of what changed in this commit.

More details of the changes, if required.
```

### Pull request names

Currently, there are not standard naming conventions for pull requests. I propose the following structure:

```
<indicator> TICKET-number: Short description of changes.
```

Where the `<indicator>` is an emoji indicating the type of the pull request

* 🔩: architectural changes (`:nut_and_bolt:`)
* 🔧: refactoring (`:wrench:`)
* ♻️: code cleanup (`:recycle:`)
* 🐞: bugfix (`:beetle:`)
* ✨: feature (`:sparkles:`)
* 🚒: hotfix (`:fire_engine:`)

The exception to this rule is merge pull requests, which will be in the form

`<origin>`➡️`<target>`

Example pull request titles:

* ♻️ `DM-242: Removed some deprecated code in preparation for analytics module.`
* `🐞 DM-264: Fixed Active Rewards dashboard crashes.`
* `✨ HLD-38: Added Scanning Guidelines to Healthy Dining.`
* `🔧 PEG-274: Refactored ContactUtil into health specific package.`
* `🚒 PEG-1025: Fixed boot receiver attempting to lazy load perms.`
* `🔩 DM-242: Added implementation for new analytics module.`
* `release/7.4 `➡️ `develop`

## Decision Outcome

Accepted - we'll be building something to enforce these rules.
