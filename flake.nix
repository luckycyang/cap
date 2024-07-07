{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-24.05";
    systems.url = "github:nix-systems/default";
    rust-overlay.url = "github:oxalica/rust-overlay";
    devenv.url = "github:cachix/devenv";
    devenv.inputs.nixpkgs.follows = "nixpkgs";
  };

  nixConfig = {
    extra-trusted-public-keys = "devenv.cachix.org-1:w1cLUi8dv3hnoSPGAuibQv+f9TZLr6cv/Hm9XgU50cw=";
    extra-substituters = "https://devenv.cachix.org";
  };

  outputs = { self, nixpkgs, devenv, systems, rust-overlay, ... } @ inputs:
    let
      forEachSystem = nixpkgs.lib.genAttrs (import systems);
    in
    {
      packages = forEachSystem (system: {
        devenv-up = self.devShells.${system}.default.config.procfileScript;
      });

      devShells = forEachSystem
        (system:
          let
            overlays = [(import rust-overlay)];
            pkgs = import nixpkgs {inherit system overlays;};
            rustpkg = pkgs.rust-bin.selectLatestNightlyWith (toolchain:
              toolchain.default.override {
              extensions = ["rust-src" "rustfmt" "clippy"]; # rust-src for rust-analyzer
              targets = ["x86_64-unknown-linux-gnu" "riscv32i-unknown-none-elf"];
            });
          in
          {
            default = devenv.lib.mkShell {
              inherit inputs pkgs;
              modules = [
                {
                  env."CHISEL_FIRTOOL_PATH" = "${pkgs.circt}/bin";
                  languages.scala.enable = true;
                  languages.scala.package = pkgs.scala_2_12;
                  packages = [pkgs.mill rustpkg pkgs.rocmPackages.llvm.llvm pkgs.toybox pkgs.python313];
                }
              ];
            };
          });
    };
}
