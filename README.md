# WePayU — Sistema de Folha de Pagamento

Projeto da disciplina de Projeto de Sistemas de Informação (UFAL). Esta versão implementa as **User Stories 1 a 10**, incluindo persistência, undo/redo, escolha de agenda de pagamento (US9) e criação de agendas customizadas (US10).

## Estado dos testes

A suíte incluída no repositório passa integralmente:

- US1–US8 e persistências: **590 verificações**;
- US9 + US9_1: **104 verificações**;
- US10 + US10_1: **92 verificações**;
- total: **786 verificações, 0 falhas**.

> O arquivo `tests/us9.txt` recebido tinha um erro de digitação em uma linha (`1292,30totalFolha`). Foi corrigido apenas o espaço ausente (`1292,30 totalFolha`), sem alterar o caso de teste nem o valor esperado.

## Como rodar

### Linux / macOS

```bash
./rodar-testes.sh
```

Ou manualmente:

```bash
rm -rf bin && mkdir bin
javac -encoding UTF-8 -d bin -cp lib/easyaccept.jar $(find src -name "*.java")
java -cp bin:lib/easyaccept.jar easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade tests/us10.txt
```

### Windows

Use `rodar-testes.bat` ou compile pelo PowerShell:

```powershell
Remove-Item -Recurse -Force bin -ErrorAction SilentlyContinue
New-Item -ItemType Directory bin | Out-Null
javac -encoding UTF-8 -d bin -cp lib\easyaccept.jar (Get-ChildItem -Recurse -Filter *.java src | % FullName)
java -cp "bin;lib\easyaccept.jar" easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade tests\us10.txt
```

Pela IDE, executar a classe `Main` sem argumentos roda **todos** os testes (us1 a us10, com os `usN_1` em JVM separada); com um argumento (ex.: `tests/us7.txt`) roda só aquele arquivo.

Os arquivos `usN_1.txt` devem rodar **em outra JVM**, imediatamente depois do `usN.txt`, porque verificam a persistência em `wepayu.db`. Os scripts `rodar-testes.sh` e `rodar-testes.bat` já fazem isso.

## Arquitetura

```text
src/br/ufal/ic/p2/wepayu/
├── Facade.java          # interface fina usada pelo EasyAccept
├── agenda/              # regras e fábrica de agendas de pagamento
├── excecoes/            # hierarquia WePayUException + uma classe por erro
├── historico/           # undo/redo (Memento)
├── models/              # entidades e regras polimórficas dos empregados
├── persistencia/        # serialização e arquivo wepayu.db
├── relatorio/           # geração do relatório da folha
├── repositorio/         # armazenamento de empregados e agendas
├── servicos/            # casos de uso: cadastro, alteração, folha, consultas...
└── util/                # parsing e formatação
```

### Decisões principais

**Responsabilidades separadas.** A antiga classe monolítica de sistema foi removida. Cadastro, lançamentos, consultas, folha, relatório, histórico e persistência ficam em componentes diferentes. A `Facade` somente verifica se o sistema está aberto e encaminha cada comando ao serviço apropriado.

**Polimorfismo no cálculo.** Cada subtipo de `Empregado` calcula seu próprio pagamento e produz seu próprio `Contracheque`; a folha não usa cadeias de `instanceof` para decidir como pagar horistas, assalariados e comissionados.

**Encapsulamento.** Os atributos do empregado são privados. Cartões de ponto e vendas permanecem internos ao modelo e são manipulados por métodos de domínio. As taxas de serviço expostas pelo sindicato são somente leitura.

**Troca de tipo sem perda de histórico.** Cartões de ponto e vendas são preservados quando um empregado muda de tipo e volta ao tipo anterior.

**Undo/redo.** O `GerenciadorHistorico` guarda snapshots serializados da base antes de cada comando mutável. Se um comando falha, o estado anterior é restaurado e a operação não entra na pilha de undo.

**Persistência.** A base completa (empregados + agendas customizadas) é serializada em `wepayu.db`. Arquivo inexistente inicia uma base nova; arquivo existente e corrompido gera erro em vez de ser silenciosamente substituído por uma base vazia.

**Agendas (US9/US10).** Existem por padrão `semanal 5`, `mensal $` e `semanal 2 5`. Novas agendas aceitam `semanal D`, `semanal N D`, `mensal D` e `mensal $`, com as faixas exigidas pelos testes. O período de cálculo vem da própria agenda, o que permite consultar `totalFolha` em datas independentes sem depender de uma chamada anterior de `rodaFolha`.

**Validação nos modelos.** Salário, comissão e taxa sindical nulos ou negativos são recusados pelo próprio modelo com a exceção específica; cartões de ponto, vendas e taxas de serviço são imutáveis e não aceitam valores nulos. A data de referência do calendário (1/1/2005) fica concentrada em `CalendarioFolha`.

**Valores monetários.** O sistema usa `BigDecimal` e trunca para duas casas onde os testes de aceitação exigem esse comportamento.
