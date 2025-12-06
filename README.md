# 👨‍🍳 ChefAl - Sugestor de Receitas Inteligente

O **ChefAl** é uma aplicação Java que utiliza Inteligência Artificial Generativa (OpenAI GPT-4o-mini) para atuar como um assistente culinário. O sistema aplica conceitos sólidos de **Orientação a Objetos** para sugerir receitas baseadas nos ingredientes que o usuário tem em casa.

---

## 🛠️ Configuração e Instalação

### 1. Pré-requisitos
* Java JDK 11 ou superior.
* Uma chave de API da OpenAI (com créditos ativos).

### 2. Configurando a Chave de API (Segurança)
O projeto não contém a chave de API no código-fonte por segurança. Para rodar:
1. Crie um arquivo chamado `config.properties` na raiz do projeto.
2. Adicione sua chave no formato:
   ```properties
   API_KEY=sk-proj-sua-chave-aqui...