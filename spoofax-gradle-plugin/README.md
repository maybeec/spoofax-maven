# Spoofax Gradle Plugin

Gradle plugins to build Spoofax languages, or use Spoofax languages in
your project.

## Spoofax Plugin

The Spoofax plugin allows you to use Spoofax languages in your Gradle
project.

### Usage

```gradle
buildscript {
  repositories {
    maven {
      url "http://artifacts.metaborg.org/content/repositories/in4303/"
    }
  }
  dependencies {
    classpath "org.metaborg:spoofax-gradle-plugin:2.0.0-SNAPSHOT"
  }
}

apply plugin: 'org.metaborg.spoofax'

dependencies {
  spoofaxCompile 'org.metaborg:metaborg-fj:1.0-SNAPSHOT'         // compiler language
  classpath      'org.metaborg:metaborg-fs-runtime:1.0-SNAPSHOT' // java dependency
}
```

### Tasks

| Name               | Description                          |
|--------------------|--------------------------------------|
| compileSpoofax     | Compile all Spoofax language sources |
| compileLANGUAGE    | Compile LANGUAGE sources             |

The plugin adds dependencies on the `build` task.
- compileSpoofax --> compileJava

### Notes

- Does not assume anything about project layout.
- Language dependencies specified in `build.gradle`.
- `compileSpoofax` compiles all `spoofaxCompile` dependencies, ensures
  correct order.
- Not much we can do about output location at the moment?

## Spoofax Meta Plugin

The Spoofax Meta Plugin allows you to build Spoofax languages using
Gradle.

### Usage

```gradle
buildscript {
  repositories {
    maven {
      url "http://artifacts.metaborg.org/content/repositories/in4303/"
    }
  }
  dependencies {
    classpath "org.metaborg:spoofax-gradle-plugin:2.0.0-SNAPSHOT"
  }
}

apply plugin: 'org.metaborg.spoofax.meta'

dependencies {
  spoofaxCompile 'org.metaborg:org.metaborg.lang.meta.esv:2.0.0-SNAPSHOT'    // compiler language
  spoofaxSource  'org.metaborg:org.metaborg.lang.meta.analysis:1.0-SNAPSHOT' // source library
}
```

### Tasks

| Name                 | Description                          |
|----------------------|--------------------------------------|
| compileSpoofaxMeta   | Compile Spoofax language             |
| createSpoofaxProject | Create initial Spoofax project       |

The plugin adds dependencies on the `build` task.
- compileSpoofaxMeta

### Notes

- Requires fixed project layout, and `metaborg.yaml` file.
- Meta-language dependencies in `metaborg.yaml`.
- Implies the Spoofax plugin.
- Provides build cycle task, with preconfigured task order, source and
  target directories.
- Provides new project task.
- Implies Java plugin.
- Can we allow custom build steps to be included in the build cycle?
