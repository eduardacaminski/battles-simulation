Simulador de Batalhas por Turnos

Jogo de batalhas por turnos no console, feito em Java como projeto final da disciplina de Programação Orientada a Objetos (Análise e Desenvolvimento de Sistemas, IFRS).

O jogador cadastra personagens de três tipos (Guerreiro, Mago e Arqueiro), monta uma equipe de até 4 integrantes e enfrenta inimigos gerados aleatoriamente. Os personagens e o histórico de batalhas ficam salvos em arquivos de texto, então os dados continuam lá quando o programa é aberto de novo.

Sumário
Funcionalidades
Regras do jogo
Conceitos de POO aplicados
Estrutura do projeto
Diagrama de classes
Como executar
Se o projeto não abrir no Eclipse
Arquivos de dados
Documentação
Funcionalidades
Opção	O que faz
1 - Cadastrar personagem	Escolhe o tipo e o nome. Nomes vazios, repetidos ou com ; são recusados.
2 - Listar personagens	Mostra todos com tipo, vida, ataque, defesa e situação (Ativo/Derrotado).
3 - Consultar personagem	Mostra os atributos e a descrição da habilidade especial.
4 - Iniciar batalha	Monta a equipe (1 a 4) e enfrenta o mesmo número de inimigos.
5 - Ver histórico	Lista as batalhas salvas: data, equipes, resultado e rodadas.
6 - Descansar	Recupera a vida de todos os personagens.
0 - Salvar e sair	Grava os personagens e encerra.

Toda entrada do teclado é validada: o programa nunca fecha por causa de uma digitação ou jogada inválida.

Regras do jogo

Turno: cada herói vivo age uma vez por rodada (ataque normal, habilidade especial ou item). Depois, os inimigos agem sozinhos, com 40% de chance de usar a habilidade.

Dano: dano = máx(1, ataque − defesa). Todo golpe tira pelo menos 1 de vida.

Tipo	Vida	Ataque	Defesa	Habilidade	Recarga
Guerreiro	130	16	10	Golpe Pesado: 150% do ataque (24 de poder)	2 rodadas
Mago	85	7	4	Bola de Fogo: 32 de dano mágico, ignora metade da defesa	3 rodadas
Arqueiro	95	14	6	Tiro Preciso: ignora a defesa, 35% de chance de crítico (dano ×2)	2 rodadas
Item	Efeito	Quantidade inicial
Poção de Vida	Cura até 40 de vida de um aliado vivo e ferido	3
Bomba	25 de dano em um inimigo, ignorando a defesa	2
Conceitos de POO aplicados
Conceito	Onde aparece
Classe abstrata	Personagem e Item: não podem ser instanciadas
Herança	Guerreiro, Mago e Arqueiro estendem Personagem; PocaoVida e Bomba estendem Item
Interfaces	Atacante (implementada por Personagem) e Usavel (implementada por Item)
Polimorfismo	A Batalha chama usarHabilidade e usar sem saber o tipo real do objeto
Encapsulamento	Atributos private, sem setters públicos; listas devolvidas somente para leitura
Template Method	usarHabilidade e usar são final e chamam métodos abstratos das filhas
Factory	FabricaPersonagens cria o objeto certo a partir do tipo escrito
Sobrecarga	Dois construtores em cada personagem e dois métodos criar na fábrica
Exceção personalizada	AcaoInvalidaException (checked) para jogadas inválidas
Coleções	List/ArrayList para personagens e equipes; Map/LinkedHashMap no inventário
Persistência	Leitura e gravação em .txt com BufferedReader/BufferedWriter e try-with-resources
MVC	Pacotes modelo, controle, visao e persistencia; só o Menu usa a tela e o teclado
Estrutura do projeto
SimuladorBatalhas/
├── src/simulador/
│   ├── Main.java                      # ponto de entrada
│   ├── excecoes/
│   │   └── AcaoInvalidaException.java
│   ├── modelo/                        # classes do jogo e suas regras
│   │   ├── Atacante.java              # interface
│   │   ├── Usavel.java                # interface
│   │   ├── Personagem.java            # classe abstrata
│   │   ├── Guerreiro.java
│   │   ├── Mago.java
│   │   ├── Arqueiro.java
│   │   ├── Item.java                  # classe abstrata
│   │   ├── PocaoVida.java
│   │   ├── Bomba.java
│   │   ├── Inventario.java
│   │   ├── Equipe.java
│   │   ├── FabricaPersonagens.java
│   │   └── RegistroBatalha.java
│   ├── controle/
│   │   ├── Batalha.java               # regras do turno
│   │   └── GerenciadorPersonagens.java
│   ├── persistencia/
│   │   ├── RepositorioPersonagens.java
│   │   └── RepositorioHistorico.java
│   └── visao/
│       └── Menu.java                  # única classe com Scanner e System.out
├── dados/
│   ├── personagens.txt
│   └── historico.txt
└── docs/
    ├── documentacao.pdf
    ├── slides.pdf
    └── uml/
Diagrama de classes

O diagrama completo está em docs/uml/0-diagrama-completo.png (versão em PDF: 0-diagrama-completo.pdf). Abaixo, ele dividido em partes.

Como ler: triângulo vazio com linha cheia = herança (extends); triângulo vazio com linha tracejada = implementa interface; losango vazio = agregação; seta tracejada = depende de / usa. Antes de cada membro: - private, + public, # protected. Sublinhado = static; itálico = abstract.

1. Personagens

Diagrama de classes: personagens

2. Itens e inventário

Diagrama de classes: itens

3. Batalha

Diagrama de classes: batalha

4. Menu e arquivos

Diagrama de classes: menu e arquivos

Como executar

Requisito: JDK 11 ou superior (testado com Java 21). O código usa String.isBlank() e FileReader(File, Charset), que existem a partir do Java 11.

No Eclipse
File → Import → General → Existing Projects into Workspace → Next.
Em Select root directory, escolha a pasta do projeto → Finish.
Abra src/simulador/Main.java e clique em Run.
Pelo terminal

Execute na pasta raiz do projeto (onde ficam src e dados), porque o programa procura os arquivos em dados/.

Windows (PowerShell):

powershell
javac -encoding UTF-8 -d bin (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp bin simulador.Main

Linux / macOS:

bash
javac -encoding UTF-8 -d bin $(find src -name "*.java")
java -cp bin simulador.Main
Se o projeto não abrir no Eclipse

Se o projeto foi criado num computador com uma versão do Java diferente da sua, o Eclipse pode mostrar tudo em vermelho (até String aparece como erro) ou dar erros como Unbound classpath container, UnsupportedClassVersionError ou FindException ao rodar. O código não precisa de mudança; é só ajustar a configuração:

Botão direito no projeto → Build Path → Configure Build Path → Libraries.
Selecione JRE System Library (se estiver unbound) → Edit → Workspace default JRE → Finish.
No menu da esquerda da mesma janela, abra Java Compiler e desmarque Enable project specific settings (ou ajuste o Compiler compliance level para a versão do Java instalada). Apply and Close.
Se existir src/module-info.java, apague o arquivo.
Project → Clean... → Clean, para recompilar tudo com o Java instalado.
Arquivos de dados

Ficam na pasta dados/ e são criados automaticamente na primeira gravação, se não existirem.

personagens.txt: um personagem por linha, no formato tipo;nome;vidaAtual. É reescrito a cada salvamento.

# tipo;nome;vidaAtual
Guerreiro;Thor;130
Mago;Merlin;61

historico.txt: uma linha por batalha, acrescentada no fim do arquivo (modo append), no formato dataHora;equipe;inimigos;resultado;inimigosDerrotados;rodadas.

18/11/2026 13:56:06;Thor, Merlin, Legolas;Lobo Sombrio 1, Goblin 2, Goblin 3;VITÓRIA;3;9

Linhas com formato inválido não interrompem a leitura: viram um aviso na tela e são ignoradas.

Documentação
Documentação completa (PDF): funcionamento, decisões de projeto, regras, persistência, exceções, requisitos e roteiro de demonstração.
Slides da apresentação (PDF)
Diagramas UML
Autora

Eduarda Caminski Roy, Análise e Desenvolvimento de Sistemas, IFRS.
