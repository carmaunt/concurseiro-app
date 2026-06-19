# Prompt mestre para importacao de questoes

Use o prompt abaixo, preenchendo os dados da solicitacao no final.

```text
Voce esta trabalhando nos repositorios do projeto O Concurseiro:

- Aplicativo Android: /home/mauricio/projetos/concurseiro-app
- Backend/API: /home/mauricio/projetos/concurseiro-api
- Painel administrativo: /home/mauricio/concurseiro-admin/admin-panel
- PDFs locais: /home/mauricio/projetos/concurseiro-app/local-pdfs

Sua tarefa e extrair questoes de uma prova em PDF, confronta-las com o gabarito oficial, gerar um lote para revisao e, somente depois de validacao explicita, cadastra-las pela API. Leia o codigo atual antes de agir, pois contratos e endpoints podem evoluir. Nunca exponha credenciais, grave segredos em arquivos ou crie scripts permanentes sem solicitacao.

## Fluxo obrigatorio

1. Localize o PDF da prova e o PDF do gabarito oficial em `local-pdfs`.
2. Analise somente o intervalo solicitado. Nao adiante questoes de outro lote.
3. Extraia texto e imagens, mas confira visualmente cada pagina. Nao confie apenas em OCR.
4. Consulte a prova, os catalogos, os textos de apoio e os enunciados ja existentes.
5. Gere apenas a previa Markdown. Nesta fase, nao altere banco, catalogos ou arquivos de imagem remotos.
6. Aguarde uma autorizacao inequivoca, como "validado", "vamos lancar" ou equivalente.
7. Apos a autorizacao, consulte novamente os registros reutilizaveis e cadastre o lote.
8. Verifique pela API tudo o que foi salvo e informe o mapeamento numero original -> `idQuestion`.

Nunca interprete um pedido de "proximo lote" como autorizacao para cadastrar. Ele autoriza somente a criacao da previa.

## Fidelidade a prova

- O PDF da prova e a fonte do conteudo; o PDF do gabarito e a unica fonte das respostas. Nao deduza nem corrija o gabarito por conhecimento proprio.
- Preserve grafia, acentos, pontuacao, maiusculas, paragrafos e destaques relevantes.
- Remova apenas artefatos editoriais: cabecalho, rodape, numero de pagina, marca de corte e numeracao visual da questao.
- Confira especialmente `0/O`, `1/l/I`, sinais `≤`, `≥`, `≠`, letras gregas, expoentes, indices, parenteses e formulas.
- Nao inclua "Questao 97" no texto salvo. O numero da prova serve apenas para controle da importacao.
- Nao invente explicacoes, titulos visiveis, alternativas, trechos ausentes ou correcoes gramaticais.

## Separacao dos campos

- `texto de apoio`: contexto compartilhado, texto-base, tabela, codigo, imagem, grafico ou figura necessario para uma ou mais questoes.
- `enunciado`: comando da questao, por exemplo, "Julgue o item subsequente."
- `questao`: afirmacao ou pergunta especifica que o candidato deve responder.
- `alternativas`: opcoes de resposta, sem misturar o enunciado ou o texto da questao.

Nao repita o texto de apoio no enunciado nem na questao. Quando o PDF trouxer um bloco como "Considerando o texto..., julgue os itens subsequentes", deixe o contexto no texto de apoio e use em cada registro um enunciado singular e completo, como "Considerando o texto de apoio, julgue o item subsequente." O enunciado deve sempre estar no singular.

## Formatacao suportada

- Negrito: `**texto**`.
- Italico: `*texto*`.
- Aplique negrito e italico somente onde aparecem ou sao semanticamente exigidos pela prova.
- Para variaveis matematicas em italico, use formas como `*X*` e `*f*(*x*)`.
- Use caracteres Unicode para formulas simples, como `γ`, `≤`, `²` e `×`. Nao use LaTeX, pois o aplicativo nao o renderiza.
- Evite `*` como operador de multiplicacao em texto Markdown; use `×`. Dentro de conteudo do tipo `CODIGO`, preserve o asterisco original.
- Preserve quebras e indentacao de codigo exatamente como no PDF.

## Reutilizacao obrigatoria

- Antes de criar um enunciado, pesquise em `GET /api/v1/enunciados?texto=...`. Se houver correspondencia textual exata, use `enunciadoId` e envie `enunciado: null`.
- Nao reutilize enunciado apenas por semelhanca. Diferencas de sentido, referencia, pontuacao ou singular/plural devem ser avaliadas.
- Antes de criar texto de apoio, pesquise em `GET /api/v1/textos-apoio?titulo=...`. Se for o mesmo titulo, tipo, conteudo e JSON, use `textoApoioId`.
- Questoes do mesmo bloco devem compartilhar o mesmo `textoApoioId` e, quando aplicavel, o mesmo `enunciadoId`.
- Se nao existir enunciado exato, envie o texto novo; o backend fara a deduplicacao por hash.
- Se nao existir texto de apoio exato, crie-o uma unica vez e reutilize o ID retornado nas demais questoes.

## Catalogos

- Pesquise e reutilize disciplina, assunto e subassunto semanticamente corretos.
- Nunca escolha um assunto ou subassunto aproximado apenas porque o correto ainda nao existe.
- Se o assunto correto nao existir, marque-o como `NOVO` na previa. Depois da validacao, cadastre-o vinculado a disciplina correta.
- Se o subassunto correto nao existir, marque-o como `NOVO` na previa. Depois da validacao, cadastre-o vinculado ao assunto correto.
- Antes de criar, pesquise novamente para evitar duplicidade por diferenca de caixa, acento, singular ou plural.
- Confirme que o subassunto pertence ao assunto informado. Nao classifique uma questao pelo tema das questoes vizinhas.
- Os endpoints atuais de criacao sao `POST /api/v1/admin/catalogo/assuntos` e `POST /api/v1/admin/catalogo/subassuntos`.
- Reutilize tambem banca, instituicao e prova existentes. Ao lancar questoes de uma prova cadastrada, prefira `POST /api/v1/provas/{provaId}/questoes`, para herdar ano, cargo, nivel, banca, instituicao e modalidade.

## Tipos de texto de apoio

- `TEXTO`: prosa comum, com Markdown para negrito e italico.
- `CODIGO`: codigo-fonte ou trecho tecnico cuja indentacao deve ser preservada. Salve em `textoApoioConteudo`; nao use cercas Markdown.
- `TABELA`: use quando os dados puderem ser representados fielmente por colunas e linhas. Grave:
  - `textoApoioJson`: `{"colunas":["A","B"],"linhas":[["1","2"]],"rodape":"opcional"}`
  - `textoApoioConteudo`: versao textual de compatibilidade, com celulas separadas por ` | `.
- `IMAGEM`: use para diagramas, telas, graficos, figuras ou tabelas complexas que perderiam informacao ao serem convertidas.

Use o titulo original quando ele existir. Se a prova nao apresentar titulo, deixe-o nulo em vez de inventar texto que aparecera para o usuario.

Para tabela, nao use imagem quando uma estrutura de linhas e colunas preservar todo o conteudo. Se houver celulas mescladas, diagramacao essencial ou destaques dentro das celulas que o renderer estruturado nao reproduz, prefira imagem. Em conteudo misto de prosa e tabela, use `TEXTO` com tabela Markdown quando isso preservar tudo; caso contrario, sinalize a limitacao na previa antes de cadastrar.

Para imagem:

- Recorte somente a area relevante, sem cabecalhos, rodapes ou numero da questao.
- Preserve legibilidade e proporcao; nao estique nem reduza agressivamente.
- Aceite PNG, JPEG ou WebP.
- Envie por `POST /api/v1/admin/textos-apoio/imagens`, que armazena no R2 e retorna o texto de apoio.
- Forneca texto alternativo objetivo e obrigatorio, com ate 500 caracteres.
- Nao monte manualmente URL, dimensoes ou `conteudoJson` quando o upload puder gera-los.
- Se uma questao tiver varias figuras inseparaveis e houver apenas um texto de apoio, gere um unico recorte composto preservando ordem, espacamento e disposicao originais.

## Modalidade e gabarito

- Certo/errado: `modalidade = CERTO_ERRADO`, alternativas no padrao ja adotado `Certo\nErrado` e gabarito `C` ou `E`.
- Multipla escolha A-D: `modalidade = A_D`, alternativas `A)` a `D)` e gabarito `A` a `D`.
- Multipla escolha A-E: `modalidade = A_E`, alternativas `A)` a `E)` e gabarito `A` a `E`.
- Questao anulada: gabarito `X`. Nao substitua anulacao por resposta presumida.

## Arquivo de previa

Crie `local-pdfs/importacao-<prova>-<disciplina-ou-multidisciplinar>-qNNN-NNN.preview.md`.

A previa deve conter:

1. Identificacao da prova, intervalo, paginas consultadas e arquivo de gabarito.
2. Resumo em tabela: numero, gabarito, disciplina, assunto, subassunto, enunciado e texto de apoio, indicando `REUTILIZAR ID`, `NOVO` ou `SEM APOIO`.
3. Para cada questao: texto de apoio completo ou referencia ao ID, tipo do apoio, enunciado completo ou ID, questao, alternativas, gabarito e catalogos.
4. Indicacao explicita de negritos, italicos, formulas, tabelas, codigos e imagens conferidos visualmente.
5. Lista dos novos assuntos/subassuntos propostos e seus respectivos pais.
6. Alertas sobre qualquer ambiguidade. Nao esconda duvidas nem escolha silenciosamente.

Ao terminar a previa, pare e aguarde validacao.

## Lancamento apos validacao

- Autentique-se pela API sem exibir segredos.
- Verifique antes se as questoes ja existem na prova, comparando o texto especifico e o contexto, para impedir duplicacao em uma nova execucao.
- Crie primeiro os catalogos aprovados; depois imagens/textos de apoio; por fim as questoes.
- Lance sequencialmente, registre cada resposta e pare no primeiro erro. Nao repita cegamente o lote, pois parte dele pode ter sido salva.
- Em correcoes posteriores, atualize a questao existente por `PUT /api/v1/admin/questoes/{idQuestion}`; nao apague e recrie, para preservar o ID e relacionamentos.
- Depois do cadastro, recupere cada questao pela API e compare enunciado, item, apoio, formatacao, catalogos, modalidade e gabarito com a previa.
- Para tabela, codigo ou imagem, valide tambem a renderizacao no aplicativo, preferencialmente no telefone conectado.
- Informe quantas questoes foram criadas, IDs retornados, enunciados e apoios reutilizados, catalogos criados e eventuais pendencias.

## Solicitacao atual

- Prova: [IDENTIFICACAO DA PROVA]
- PDF da prova: [ARQUIVO OU "LOCALIZAR EM local-pdfs"]
- PDF do gabarito: [ARQUIVO OU "LOCALIZAR EM local-pdfs"]
- Questoes: [INICIO] a [FIM]
- Acao: [GERAR PREVIA | LANCAR LOTE JA VALIDADO | CORRIGIR QUESTOES]
- Observacoes adicionais: [OPCIONAL]
```
