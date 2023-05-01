#!/usr/bin/env kotlin
@file:DependsOn("it.krzeminski:github-actions-kotlin-dsl:0.40.0")

import it.krzeminski.githubactions.actions.actions.CheckoutV3
import it.krzeminski.githubactions.actions.actions.SetupJavaV3
import it.krzeminski.githubactions.actions.gradle.GradleBuildActionV2
import it.krzeminski.githubactions.domain.RunnerType
import it.krzeminski.githubactions.domain.triggers.WorkflowDispatch
import it.krzeminski.githubactions.domain.triggers.WorkflowDispatch.Input
import it.krzeminski.githubactions.domain.triggers.WorkflowDispatch.Type.String
import it.krzeminski.githubactions.dsl.expressions.Contexts
import it.krzeminski.githubactions.dsl.expressions.expr
import it.krzeminski.githubactions.dsl.workflow
import it.krzeminski.githubactions.yaml.writeToFile

val OSSRH_USERNAME by Contexts.secrets
val OSSRH_PASSWORD by Contexts.secrets
val ORG_GRADLE_PROJECT_signingKey by Contexts.secrets
val ORG_GRADLE_PROJECT_signingPassword by Contexts.secrets

workflow(
  name = "Publish",
  on = listOf(
    WorkflowDispatch(
      mapOf("RELEASE_VERSION" to Input("The release version", true, String))
    )
  ),
  sourceFile = __FILE__.toPath(),
  env = linkedMapOf(
    "OSSRH_USERNAME" to expr { OSSRH_USERNAME },
    "OSSRH_PASSWORD" to expr { OSSRH_PASSWORD },
    "ORG_GRADLE_PROJECT_signingKey" to expr { ORG_GRADLE_PROJECT_signingKey },
    "ORG_GRADLE_PROJECT_signingPassword" to expr { ORG_GRADLE_PROJECT_signingPassword },
    "RELEASE_VERSION" to expr { github["event.inputs.version"]!! }
  )
) {
  job("build", runsOn = RunnerType.UbuntuLatest) {
    uses(name = "Set up JDK", SetupJavaV3(javaVersion = "17", distribution = SetupJavaV3.Distribution.Adopt))
    uses(CheckoutV3())
    uses(
      "Run publish", GradleBuildActionV2(
        arguments = "publish"
      )
    )
  }
}.writeToFile()