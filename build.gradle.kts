plugins {
    base
}

group = "dev.valiantgiant985.borderlessmc"

tasks.named("build") {
    dependsOn(":versions:mc26_1:build", ":versions:mc26_1_1:build", ":versions:mc26_1_2:build", ":versions:mc26_2:build", ":versions:mc26_3:build")
}
