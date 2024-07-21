{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = {
    self,
    nixpkgs,
    flake-utils,
  }: let
    supportedSystems = with flake-utils.lib.system; [x86_64-linux x86_64-darwin aarch64-darwin];
    forAllSystems = f: nixpkgs.lib.genAttrs supportedSystems (system: f system);
    buildToolsVersion = "34.0.0";
  in
    flake-utils.lib.eachSystem supportedSystems
    (
      system: let
        pkgs = import nixpkgs {
          inherit system;
          config = {
            android_sdk.accept_license = true; # accept all of the sdk licenses
            allowUnfree = true; # needed to get android stuff to compile
          };
        };
        android-sdk =
          (pkgs.androidenv.composeAndroidPackages {
            #toolsVersion = "31.0.0";
            #platformToolsVersion = "35.0.1";
            buildToolsVersions = [buildToolsVersion];
            platformVersions = ["34"];
          })
          .androidsdk;
      in rec {
        devShells.default = pkgs.mkShell rec {
          ANDROID_SDK_ROOT = "${android-sdk}/libexec/android-sdk";
          GRADLE_OPTS = "-Dorg.gradle.project.android.aapt2FromMavenOverride=${ANDROID_SDK_ROOT}/build-tools/${buildToolsVersion}/aapt2";
          JAVA_HOME = "${pkgs.jdk17.home}";
          nativeBuildInputs = [pkgs.android-studio];
        };
      }
    );
}

