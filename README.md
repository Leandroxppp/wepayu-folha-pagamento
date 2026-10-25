# WePayU — Sistema de Folha de Pagamento

Projeto da disciplina de **Projeto de Programação 2 (UFAL)**.

Esta versão implementa as **User Stories 1 a 10**, incluindo persistência, undo/redo, escolha de agenda de pagamento (US9) e criação de agendas customizadas (US10).

## Estado dos testes

A suíte incluída no repositório passa integralmente:

- US1–US8 e persistências: **590 verificações aprovadas**;
- US9 + US9_1: **104 verificações aprovadas**;
- US10 + US10_1: **92 verificações aprovadas**;
- total: **786 testes/verificações aprovados, 0 falhas**.

O script de testes compila todos os arquivos `.java` antes de executar a suíte, evitando que código-fonte órfão ou quebrado passe despercebido.

> O arquivo `tests/us9.txt` recebido possuía um erro de digitação em uma linha (`1292,30totalFolha`). Foi corrigido apenas o espaço ausente (`1292,30 totalFolha`), sem alterar o caso de teste nem o valor esperado.

---

## Como executar

### Windows

A forma recomendada é executar o script:

```powershell
.\rodar-testes.bat
```

O script:

1. limpa a compilação anterior;
2. compila todos os arquivos `.java`;
3. executa US1 até US10;
4. executa os arquivos `usN_1.txt` na ordem correta;
5. preserva o banco entre `usN` e `usN_1`;
6. limpa arquivos temporários ao final.

O resultado esperado é:

```text
COMPILACAO OK

US1     -> 51 tests OK
US1_1   -> 25 tests OK
US2     -> 21 tests OK
US2_1   -> 6 tests OK
US3     -> 35 tests OK
US3_1   -> 12 tests OK
US4     -> 29 tests OK
US4_1   -> 7 tests OK
US5     -> 31 tests OK
US5_1   -> 7 tests OK
US6     -> 91 tests OK
US6_1   -> 13 tests OK
US7     -> 166 tests OK
US8     -> 96 tests OK
US9     -> 98 tests OK
US9_1   -> 6 tests OK
US10    -> 86 tests OK
US10_1  -> 6 tests OK
```

Total esperado: **786 testes OK**.

### Execução manual no PowerShell

Para compilar:

```powershell
Remove-Item -Recurse -Force bin -ErrorAction SilentlyContinue
New-Item -ItemType Directory bin -Force | Out-Null

javac -encoding UTF-8 -d bin -cp lib\easyaccept.jar `
    (Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName })
```

Para executar um teste específico:

```powershell
java -cp "bin;lib\easyaccept.jar" easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade tests\us10.txt
```

Para executar sua continuação de persistência:

```powershell
java -cp "bin;lib\easyaccept.jar" easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade tests\us10_1.txt
```

---

### Linux / macOS

Execute:

```bash
./rodar-testes.sh
```

Ou compile manualmente:

```bash
rm -rf bin
mkdir bin

javac -encoding UTF-8 -d bin -cp lib/easyaccept.jar $(find src -name "*.java")
```

Depois execute um teste:

```bash
java -cp "bin:lib/easyaccept.jar" easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade tests/us10.txt
```

---

## Persistência nos testes

Os arquivos `usN_1.txt` devem ser executados **em outra JVM e imediatamente depois do respectivo `usN.txt`**, porque verificam a persistência dos dados em `wepayu.db`.

Por exemplo:

```text
us1.txt
us1_1.txt
```

Entre essas duas execuções, o arquivo `wepayu.db` não deve ser apagado.

Antes de iniciar uma nova User Story, o banco pode ser removido para garantir um estado limpo.

Os scripts `rodar-testes.bat` e `rodar-testes.sh` já executam essa sequência automaticamente.

---

## Estrutura do projeto

```text
src/br/ufal/ic/p2/wepayu/
├── Facade.java          # interface utilizada pelo EasyAccept
├── agenda/              # regras e fábrica de agendas de pagamento
├── excecoes/            # hierarquia WePayUException e exceções específicas
├── historico/           # mecanismo de undo/redo
├── models/              # entidades e regras dos empregados
├── persistencia/        # serialização e gerenciamento do wepayu.db
├── relatorio/           # geração dos relatórios da folha
├── repositorio/         # armazenamento de empregados e agendas
├── servicos/            # casos de uso e regras da aplicação
└── util/                # parsing, datas e formatação
```

---

## Arquitetura

### Separação de responsabilidades

A antiga classe monolítica responsável por praticamente todo o sistema foi removida.

As responsabilidades agora estão distribuídas entre componentes específicos:

- cadastro e alteração de empregados;
- lançamentos;
- consultas;
- cálculo da folha;
- relatórios;
- agendas;
- histórico;
- persistência.

A `Facade` funciona como ponto de entrada utilizado pelo EasyAccept e encaminha as operações para os serviços correspondentes.

### Polimorfismo no cálculo

Cada subtipo de `Empregado` possui o comportamento necessário para calcular seu próprio pagamento e gerar seu `Contracheque`.

Dessa forma, o cálculo da folha não depende de cadeias de `instanceof` para decidir como pagar empregados:

- horistas;
- assalariados;
- comissionados.

### Encapsulamento

Os atributos dos empregados são privados.

Cartões de ponto e resultados de venda permanecem internos aos modelos e são manipulados por métodos de domínio.

As taxas de serviço do sindicato são expostas de forma somente leitura, evitando alteração externa indevida das coleções internas.

### Troca de tipo sem perda de histórico

Ao alterar o tipo de um empregado, os dados históricos compatíveis são preservados.

Assim, por exemplo:

- um horista pode virar assalariado e posteriormente voltar a ser horista sem perder cartões de ponto anteriores;
- um comissionado pode mudar de tipo e posteriormente retornar sem perder vendas anteriores.

### Undo/redo

O `GerenciadorHistorico` mantém snapshots da base antes de comandos que alteram o sistema.

Se uma operação falhar:

- o estado anterior é preservado;
- a alteração não é registrada no histórico;
- o sistema continua consistente.

Também são suportadas operações de desfazer e refazer.

### Persistência

A base completa é armazenada em:

```text
wepayu.db
```

A persistência inclui:

- empregados;
- informações sindicais;
- lançamentos;
- agendas de pagamento;
- agendas customizadas.

Quando o arquivo não existe, uma nova base é criada.

Quando existe um arquivo inválido ou corrompido, o erro é tratado explicitamente em vez de substituir silenciosamente os dados por uma base vazia.

### Agendas de pagamento — US9 e US10

O sistema possui as agendas padrão:

```text
semanal 5
mensal $
semanal 2 5
```

Também permite criar agendas customizadas nos formatos previstos pelos testes:

```text
semanal D
semanal N D
mensal D
mensal $
```

O período utilizado para o cálculo da folha é determinado pela própria agenda do empregado.

Isso permite consultar `totalFolha` de forma independente, sem exigir uma chamada anterior de `rodaFolha`.

### Validação nos modelos

Validações importantes são realizadas nos próprios modelos.

Valores inválidos, como salário, comissão ou taxa sindical nulos ou negativos, são rejeitados utilizando exceções específicas.

Cartões de ponto, vendas e taxas de serviço também protegem seus dados contra valores inválidos.

A data de referência utilizada pelas regras do calendário fica centralizada em `CalendarioFolha`.

### Valores monetários

Os cálculos monetários utilizam `BigDecimal`.

Os valores são tratados com duas casas decimais seguindo o comportamento exigido pelos testes de aceitação.

---

## Arquivos de referência da folha

A pasta:

```text
ok/
```

contém arquivos de referência utilizados pelos testes de geração da folha.

Os testes comparam os relatórios gerados pelo sistema com esses arquivos, portanto eles fazem parte da suíte e não devem ser removidos.

---

## Resultado atual

A versão atual foi validada com:

```text
786 testes/verificações aprovados
0 falhas
```

Incluindo:

- cadastro e remoção de empregados;
- alterações de dados;
- cartões de ponto;
- vendas;
- sindicato;
- folha de pagamento;
- persistência;
- undo/redo;
- agendas de pagamento;
- criação de agendas customizadas.
