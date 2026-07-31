# AdMob — configuração de produção

O aplicativo integra:

- Google Mobile Ads SDK `25.4.0`;
- User Messaging Platform (UMP) `4.0.0`;
- banner adaptativo ancorado na tela inicial;
- intersticial somente na transição para a próxima questão, após no mínimo
  8 respostas e respeitando um intervalo mínimo de 10 minutos;
- opção para rever escolhas de privacidade quando o UMP indicar que ela é
  obrigatória.

O fluxo falha de forma segura: sem consentimento válido, IDs configurados,
rede ou inventário, o aplicativo continua funcionando sem anúncios.

## 1. Criar o aplicativo e as unidades no AdMob

No painel do AdMob:

1. Cadastre o aplicativo Android com o package
   `br.com.mauricio.oconcurseiro`.
2. Crie uma unidade do tipo **Banner**.
3. Crie uma unidade do tipo **Intersticial**.
4. Copie o ID do aplicativo e os IDs das duas unidades.

Não confunda os formatos:

```text
ID do app:     ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY
ID da unidade: ca-app-pub-XXXXXXXXXXXXXXXX/ZZZZZZZZZZ
```

## 2. Configurar os IDs reais

Em desenvolvimento, o projeto sempre usa os IDs oficiais de teste do Google.
Isso evita tráfego inválido e suspensão da conta.

Para um build `release`, adicione ao `local.properties` (não versionado):

```properties
ADMOB_APP_ID=ca-app-pub-XXXXXXXXXXXXXXXX~YYYYYYYYYY
ADMOB_BANNER_AD_UNIT_ID=ca-app-pub-XXXXXXXXXXXXXXXX/BBBBBBBBBB
ADMOB_INTERSTITIAL_AD_UNIT_ID=ca-app-pub-XXXXXXXXXXXXXXXX/IIIIIIIIII
```

No CI, use variáveis de ambiente com os mesmos nomes. Quando os IDs das
unidades não existem, os anúncios permanecem desativados no release.

## 3. Publicar as mensagens de privacidade

Em **AdMob > Privacidade e mensagens**:

1. Crie e publique a mensagem de **Regulamentações europeias**.
2. Configure a mensagem aplicável a estados dos EUA, se necessário.
3. Ative o modo de consentimento e revise os parceiros de tecnologia de
   anúncios.
4. Confirme que o nome, ícone e URL da política de privacidade estão corretos.

O código consulta o UMP em toda abertura do app, exibe formulários obrigatórios
e só inicializa anúncios quando `canRequestAds()` permite.

## 4. Checklist antes de publicar

- Publique a política de privacidade atualizada.
- Atualize a seção **Segurança dos dados** no Google Play para contemplar
  endereço IP, interações, diagnósticos e identificadores processados pelo
  Google Mobile Ads SDK.
- Declare que os dados são usados/compartilhados para publicidade, análise e
  prevenção a fraude conforme a configuração real do app.
- Cadastre o aplicativo na página da loja dentro do AdMob e aguarde a análise.
- Configure `app-ads.txt` no domínio do site do desenvolvedor cadastrado na
  Play Store.
- Instale um build fechado da Play Store e valide consentimento, banner,
  intersticial, rotação, retorno do background e ausência de anúncio.
- Nunca clique nos próprios anúncios nem use IDs reais em testes locais.

## 5. Validação local

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
./gradlew lintDebug
```

O APK debug deve mostrar a marcação **Test Ad** do Google. Se não houver
inventário ou conexão, a interface não deve reservar espaço para um anúncio
com falha nem impedir a navegação.
