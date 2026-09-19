# WePayU - Sistema de Folha de Pagamento

Trabalho da disciplina de Projeto de Sistemas de Informacao (UFAL). Implementa
as User Stories 1 a 8 (primeiro milestone) do projeto WePayU: cadastro de
empregados, cartao de ponto, vendas, taxas de sindicato e a folha de
pagamento em si, com undo/redo e persistencia entre execucoes.

Passa nos 14 arquivos de teste de aceitacao (us1 a us8, com as variantes
_1 que testam persistencia), 590 verificacoes no total, sem nenhuma falha.

## Como rodar

Compilar:
```
javac -d bin -cp lib/easyaccept.jar $(find src -name "*.java")
```

Rodar um teste (exemplo us1):
```
java -cp bin:lib/easyaccept.jar easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade tests/us1.txt
```

E assim pra us2, us3... us8. Os arquivos `usN_1.txt` precisam ser rodados
numa execucao SEPARADA da JVM logo depois do `usN.txt` correspondente (sem
chamar zerarSistema antes), porque eles testam se os dados persistidos em
disco (arquivo `wepayu.db`) continuam la depois que o processo termina.

No Windows/PowerShell trocar `$(find src -name "*.java")` por
`(Get-ChildItem -Recurse -Filter *.java src | % FullName)` e o `:` do
classpath por `;`.

## Estrutura

```
src/
  Main.java                    -> roda os testes via EasyAccept (opcional)
  br/ufal/ic/p2/wepayu/
    Facade.java                -> interface usada pelos testes
    Sistema.java                -> regras de negocio (folha, cadastro, etc)
    excecoes/                   -> uma classe por tipo de erro (EmpregadoNaoExiste,
                                    SalarioNulo, TipoInvalido etc), todas
                                    extends Exception com a mensagem fixa
    models/                     -> Empregado e subtipos, CartaoPonto,
                                    ResultadoVenda, TaxaServico,
                                    MembroSindicato, MetodoPagamento
    util/Formatos.java          -> parsing/formatacao de numero e data
```

A `Sistema` nao sabe nada sobre a linguagem de scripts do EasyAccept, quem
traduz comando -> metodo e a `Facade`.

Cada metodo declara exatamente quais excecoes pode lancar (`throws
NomeNulo, EnderecoNulo, TipoInvalido, ...`), sem usar `Exception`
generico em nenhum lugar - cada erro tem sua propria classe em
`excecoes/`, com a mensagem fixa no construtor.

## Algumas decisoes de implementacao

**Persistencia**: no `encerrarSistema` o objeto `Sistema` inteiro e
serializado (serializacao padrao do Java, nao usei XMLEncoder) pro arquivo
`wepayu.db`. Quando a `Facade` e criada de novo, ela tenta carregar esse
arquivo; se nao existir, comeca vazio. E assim que os testes `usN_1`
conseguem ver o que foi feito no `usN` anterior.

**Undo/redo**: implementei como memento. Antes de rodar um comando que
altera o estado, a `Facade` clona o `Sistema` inteiro e guarda a copia
antiga numa pilha. So troca a referencia atual se o comando terminar sem
erro - assim um comando que da exception nunca entra na pilha de undo,
que e uma das exigencias da user story 8.

**Arredondamento**: os valores calculados (comissao, parte fixa do
comissionado etc) sao truncados em 2 casas, nao arredondados. Percebi
isso comparando com os valores dos arquivos `ok/*.txt` - por exemplo o
calculo `salario*24/52` da 692,3076... e o esperado e 692,30, nao 692,31.

**Contracheque nao pode ficar negativo**: se o desconto (sindicato +
taxas de servico pendentes) for maior que o salario bruto do periodo, o
desconto fica limitado ao bruto (liquido fica 0) e o resto continua
pendente pra ser cobrado depois.

**Rodar a folha duas vezes pra mesma data**: o teste us7 faz isso de
proposito e espera o mesmo resultado nas duas vezes. Pra isso guardo em
cada empregado qual foi o ultimo resultado de folha calculado e pra qual
data - se pedirem a mesma data de novo, devolve o que ja tinha calculado
em vez de recalcular (recalcular do zero ia dar diferente, porque o
periodo ja teria sido "consumido" na primeira vez).

**Agendas de pagamento** (simplificacao do milestone 1): horista recebe
toda sexta, comissionado a cada duas sextas a partir de 14/1/2005,
assalariado no ultimo dia util do mes (sem considerar feriado).

## Fora do escopo

O enunciado so define o primeiro milestone (US 1 a 8); os outros dois
estao como "TBD". Coisas que ficam de fora por isso: pagamento
proporcional pro empregado recem contratado, agendas de pagamento
customizadas (US 9/10), outros impostos, 13o salario.
