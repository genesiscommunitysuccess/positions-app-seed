# {{kebabCase pkgName}}

This project has been created from the Genesis Positions Application Seed. Our seeds allow users to quickly bootstrap
their projects. Each seed adheres to strict Genesis best practices, and has passed numerous performance, compliance and
accessibility checks. 

{{!

# Testing

To quickly test this seed, run:

```
npx -y @genesislcap/genx@latest init myapp -x --ref v1.3.0 --no-shell && \
npx -y @genesislcap/genx@latest add myapp -s positions-app-seed
```

To test that it compiles:

```
./gradlew assemble
```

Local `positions-app-seed` folder will be used, if available. To only use remote version, pass `--remote`.
}}

# Introduction

## Next Steps

If you need an introduction to the Genesis platform and its modules it's worth heading [here](https://learn.genesis.global/docs/getting-started/quick-start/).

## Project Structure

This project contains **client** and **server/jvm** sub-project which contain respectively the frontend and the backend code

### Server

The server code for this project can be found [here](./server/jvm/server/README.md).
It is built using a DSL-like definition based on the Kotlin language: GPAL.

## Web Client

The Web client for this project can be found [here](./client/README.md). It is built using Genesis's next
generation web development framework, which is based on Web Components. Our state-of-the-art design system and component
set is built on top of [Microsoft FAST](https://www.fast.design/docs/introduction/).

# License

This is free and unencumbered software released into the public domain.

For full terms, see [LICENSE](./LICENSE)

**NOTE** This project uses licensed components listed in the next section, thus licenses for those components are required during development.

## Licensed components
Genesis low-code platform