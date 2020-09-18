{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  buildInputs = [
    pkgs.clojure
    pkgs.mkcert
    pkgs.openjdk
  ];
}
