# WePayU - Sistema de Folha de Pagamento

Trabalho da disciplina de Projeto de Sistemas de Informacao (UFAL). Implementa
as User Stories 1 a 8 (primeiro milestone) do projeto WePayU: cadastro de
empregados, cartao de ponto, vendas, taxas de sindicato e a folha de
pagamento em si, com undo/redo e persistencia entre execucoes.

Passa nos 14 arquivos de teste de aceitacao (us1 a us8, com as variantes
_1 que testam persistencia), 590 verificacoes no total, sem nenhuma falha.

## Como rodar

### Linux / Mac

Compilar:
```
javac -d bin -cp lib/easyaccept.jar $(find src -name "*.java")
```

Rodar um teste (exemplo us1):
```
java -cp bin:lib/easyaccept.jar easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade tests/us1.txt
```

### Windows (PowerShell)

Compilar:
```

javac -d bin -cp lib\easyaccept.jar (Get-ChildItem -Recurse -Filter *.java src | % FullName)
```

Rodar um teste (exemplo us1):
```
java -cp "bin;lib\easyaccept.jar" easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade tests\us1.txt
```
Atencao: as aspas em volta de `"bin;lib\easyaccept.jar"` sao obrigatorias no
PowerShell. Sem elas, o `;` é interpretado como separador de comandos e o
classpath quebra.

### Rodando todos os testes

E so trocar `us1.txt` por `us2.txt`, `us3.txt`... ate `us8.txt`. Os arquivos
`usN_1.txt` precisam ser rodados numa execucao SEPARADA da JVM logo depois
do `usN.txt` correspondente (sem chamar zerarSistema antes), porque eles
testam se os dados persistidos em disco (arquivo `wepayu.db`, criado na
pasta onde o comando roda) continuam la depois que o processo termina.

## Estrutura

```
src/
  Main.java                -> roda os testes via EasyAccept (opcional)
  br/ufal/ic/p2/wepayu/
    Facade.java             -> interface usada pelos testes
    Sistema.java            -> regras de negocio (folha, cadastro, etc)
    excecoes/               -> uma classe por tipo de erro, cada uma extends Exception com a mensagem fixa
    models/                 -> Empregado e subtipos, CartaoPonto, ResultadoVenda, TaxaServico, MembroSindicato, MetodoPagamento
    util/Formatos.java      -> parsing e formatacao de numero e data
tests/                      -> scripts de teste de aceitacao (EasyAccept)
ok/                         -> relatorios de folha de referencia, usados pelos testes (equalFiles) para comparar com o que o sistema gerou
lib/easyaccept.jar          -> biblioteca usada pra rodar os testes
```

A `Sistema` nao sabe nada sobre a linguagem de scripts do EasyAccept, quem
traduz comando -> metodo e a `Facade`.

Cada metodo declara exatamente quais excecoes pode lancar (`throws
NomeNulo, EnderecoNulo, TipoInvalido, ...`), sem usar `Exception`
generico em nenhum lugar - cada erro tem sua propria classe em
`excecoes/`, com a mensagem fixa no construtor.


A `Sistema` não sabe nada sobre a linguagem de scripts do EasyAccept, quem
traduz comando -> metodo e a `Facade`.

Cada metodo declara exatamente quais excecoes pode lancar (`throws
NomeNulo, EnderecoNulo, TipoInvalido, ...`), sem usar `Exception`
generico em nenhum lugar - cada erro tem sua propria classe em
`excecoes/`, com a mensagem fixa no construtor.

## Algumas decisoes de implementacao

**Persistencia**: no `encerrarSistema` o objeto `Sistema` inteiro e
serializado (serializacao padrao do Java, não usei XMLEncoder) pro arquivo
`wepayu.db`. Quando a `Facade` e criada de novo, ela tenta carregar esse
arquivo; se não existir, começa vazio. E assim que os testes `usN_1`
conseguem ver o que foi feito no `usN` anterior.

**Undo/redo**: implementei como memento. Antes de rodar um comando que
altera o estado, a `Facade` clona o `Sistema` inteiro e guarda a cópia
antiga numa pilha. So troca a referencia atual se o comando terminar sem
erro - assim um comando que da exception nunca entra na pilha de undo,
que e uma das exigências da user story 8.

**Arredondamento**: os valores calculados (comissão, parte fixa do
comissionado etc) sao truncados em 2 casas, não arredondados. Percebi
isso comparando com os valores dos arquivos `ok/*.txt` - por exemplo o
cálculo `salario*24/52` da 692,3076... e o esperado e 692,30, não 692,31.

**Contracheque não pode ficar negativo**: se o desconto (sindicato +
taxas de serviço pendentes) for maior que o salário bruto do período, o
desconto fica limitado ao bruto (líquido fica 0) e o resto continua
pendente pra ser cobrado depois.

**Rodar a folha duas vezes pra mesma data**: o teste us7 faz isso de
proposito e espera o mesmo resultado nas duas vezes. Pra isso guardo em
cada empregado qual foi o ultimo resultado de folha calculado e pra qual
data - se pedirem a mesma data de novo, devolve o que ja tinha calculado
em vez de recalcular (recalcular do zero ia dar diferente, porque o
periodo ja teria sido "consumido" na primeira vez).

**Agendas de pagamento** (simplificação do milestone 1): horista recebe
toda sexta, comissionado a cada duas sextas a partir de 14/1/2005,
assalariado no último dia útil do mês (sem considerar feriado).

## Fora do escopo

O enunciado só define o primeiro milestone (US 1 a 8); os outros dois
estão como "TBD". Coisas que ficam de fora por isso: pagamento
proporcional pro empregado recem contratado, agendas de pagamento
customizadas (US 9/10), outros impostos, 13o salario.

