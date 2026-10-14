# WePayU - Sistema de Folha de Pagamento

Trabalho da disciplina de Projeto de Sistemas de Informacao (UFAL). Implementa as
User Stories 1 a 10: cadastro de empregados, cartao de ponto, vendas, taxas de
sindicato, folha de pagamento, undo/redo, persistencia entre execucoes e (milestone 2)
agendas de pagamento, inclusive customizadas.

Passa em todos os testes de aceitacao (us1 a us10, com as variantes `_1` de persistencia).

## Como rodar

Linux / Mac: `./rodar-testes.sh` compila e roda tudo. Manualmente:

```
javac -d bin -cp lib/easyaccept.jar $(find src -name "*.java")
java -cp bin:lib/easyaccept.jar easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade tests/us1.txt
```

Windows (PowerShell):

```
javac -d bin -cp lib\easyaccept.jar (Get-ChildItem -Recurse -Filter *.java src | % FullName)
java -cp "bin;lib\easyaccept.jar" easyaccept.EasyAccept br.ufal.ic.p2.wepayu.Facade tests\us1.txt
```

Os `usN_1.txt` devem rodar em uma execucao SEPARADA da JVM logo depois do `usN.txt`
(sem `zerarSistema`), pois testam os dados gravados em `wepayu.db`.

## Estrutura

```
src/br/ufal/ic/p2/wepayu/
  Facade.java        -> so traduz comando do script em chamada de servico
  servicos/          -> regras de aplicacao: cadastro, atributos, alteracoes, lancamentos,
                        consultas por periodo, folha e agendas
  models/            -> Empregado (Horista, Assalariado, Comissionado), Contracheque,
                        MembroSindicato, MetodoPagamento, lancamentos
  agenda/            -> AgendaPagamento (Semanal, Mensal), FabricaAgendas, AgendasPadrao
  repositorio/       -> BaseDados, RepositorioEmpregados, RepositorioAgendas, ContextoSistema
  historico/         -> GerenciadorHistorico (undo/redo)
  persistencia/      -> ArquivoPersistencia, Serializacao
  relatorio/         -> RelatorioFolha e suas secoes
  excecoes/          -> WePayUException (classe-mae) e uma subclasse por erro
  util/              -> Entrada (texto -> numero/data) e Formatos
```

## Decisoes de projeto

- **Polimorfismo no calculo**: cada tipo de empregado monta o proprio `Contracheque`
  (`montarContracheque`); nao ha `instanceof` na folha. O relatorio usa o padrao Visitor
  (`VisitanteContracheque`) para separar os contracheques por secao.
- **Agendas (US9/US10)**: `AgendaPagamento` diz se uma data e dia de pagamento, qual foi o
  pagamento anterior e quanto do salario mensal cabe em cada pagamento
  (`semanal N D`: salario*12*N/52; `mensal`: salario inteiro). A contagem das semanas parte de
  1/1/2005 (`CalendarioFolha.DATA_INICIAL`), unica data fixa do sistema, como nos testes.
  O periodo pago vai do pagamento anterior da agenda (ou do ultimo pagamento efetivo, o que for
  mais recente) ate a data. Ao mudar o tipo do empregado a agenda volta para a padrao do novo tipo.
- **Encapsulamento**: atributos privados; os modelos validam seus proprios valores (nome, salario,
  comissao, horas, taxa...) e entregam listas somente-leitura.
- **Troca de tipo** mantem cartoes de ponto, vendas, sindicato e metodo de pagamento. Virar
  comissionado exige informar a comissao.
- **Undo/redo**: antes de cada comando guarda-se uma copia serializada da base; se o comando falha a
  base e restaurada, se da certo a copia vai para a pilha de desfazer.
- **Persistencia**: `encerrarSistema` grava `wepayu.db` (serializacao Java). Erro ao gravar vira
  `FalhaPersistenciaException`; arquivo ilegivel gera aviso no erro padrao e o sistema comeca vazio.
- **Arredondamento**: valores calculados sao truncados em 2 casas (como nos arquivos de `ok/`).
- **Contracheque nunca negativo**: se taxa sindical + taxas de servico passam do bruto, o desconto
  fica limitado ao bruto e o restante continua pendente para a proxima folha.
- **Erros**: todos estendem `WePayUException`; cada erro tem sua classe (nome terminado em `Exception`).

Obs.: `tests/us9.txt` tinha um erro de digitacao (`1292,30totalFolha`, sem espaco), corrigido nesta copia.
