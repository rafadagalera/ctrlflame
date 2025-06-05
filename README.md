# CtrlFlame - Sistema de Monitoramento e Prevenção de Queimadas

##  Sobre o Projeto

O CtrlFlame é um sistema integrado de monitoramento e prevenção de queimadas que combina dados do INPE com uma rede de sensores IoT para fornecer alertas em tempo real e análises históricas de focos de incêndio.

###  Principais Funcionalidades

1. **Monitoramento em Tempo Real**
   - Visualização de dados de sensores em tempo real
   - Medição de temperatura e umidade via sensores DHT11
   - Cálculo automático do nível de risco de incêndio
   - Alertas via WhatsApp para situações críticas

2. **Mapa de Queimadas**
   - Visualização geográfica de focos de incêndio
   - Dados históricos fornecidos pelo INPE
   - Filtros por estado, bioma e período
   - Análise de risco por região

3. **Sistema de Notificações**
   - Alertas via WhatsApp para usuários cadastrados
   - Notificações personalizadas por nível de risco
   - Integração com Twilio para envio de mensagens
   - Opt-in simplificado para recebimento de alertas

4. **Área do Usuário**
   - Cadastro e gerenciamento de perfil
   - Registro de número de telefone para alertas
   - Configuração de preferências de notificação
   - Histórico de alertas recebidos

## Como Executar o Projeto

####### IMPORTANTE #######

Configuração do Ambiente

1. Configure as variáveis de ambiente (.env)

   No arquivo chamado variaveis.txt anexado junto com a entrega estão os valores que deverão ser subsitituidos no arquivo .env (ou adicionados manualmente)

   twilio.account.sid=account_sid_fornecido 
   twilio.auth.token=auth_token_fornecido
   twilio.whatsapp.from=numero_whatsapp_fornecido

2. Instale as dependências 


3 Execute o projeto
   ./mvnw spring-boot:run

   - Abra o navegador e acesse: `http://localhost:8080`



## Estrutura do Projeto
config/      # Configurações
controller/  # Controladores
model/      # Entidades
repository/ # Repositórios
services/   # Serviços
resources/ # Templates, dados csv e estilização


## Integrantes

## Rafael Nascimento
## Carolina Machado
## Iago Diniz


