package av.borisov;

import java.util.*;

/**
 * Класс {@code DiceSession} описывает партию игры в кости.
 * 
 * <p>Играют N игроков (компьютер в списке последний).
 * Подкидываются одновременно К кубиков. Выигрывает тот, у кого большая
 * сумма очков. Кто выиграл, тот и кидает первым в следующем кону. Игра идет
 * до 7 выигрышей. Начинаете игру Вы.
 * 
 * @author Александр Борисов
 */
public class DiceSession implements Leaderboard {
	//Константы максимально возможного числа выигрышей в игре и числа граней кубика.
	private static final int MAX_WINS = 7;
	private static final int N_DICE_FACES = 6;
	
	//Переменные числа игроков и числа костей.
	private int nPlayers;
	private int kDices;
	
	//Список объектов "игрок", объект "текущий раунд"
	//и массив, хранящий игроков в том порядке,
	//в котором они ходят в следующем раунде.
	private List<Player> players = new ArrayList<Player>();
	private Round currentRound;
	private Player[] nextPlayerOrder;
	
	//Генератор псевдослучайных чисел.
	private Random rand = new Random(System.currentTimeMillis());
	
	//Компаратор для ранжирования игроков по числу побед.
	//Используется при составлении Leaderboard (см. метод getLeaderboard()).
	private Comparator<DiceSession.Player> winsComparator = 
			new Comparator<DiceSession.Player>() {
		@Override
		public int compare(DiceSession.Player p1, DiceSession.Player p2) {
			return p2.getWins()-p1.getWins();
		}
	};
	
	//Метод, заполняющий список players игроками
	private void assignPlayers() {
		for (int i = 1; i<nPlayers; i++) {
			players.add(new Player("Player"+i));
		}
		players.add(new Player("Me"));
	}
	
	/**
	 * Конструктор класса {@code DiceSession} без параметров.
	 * Создаёт партию с 4 людьми и 5 кубиками. Автоматически создаётся первый раунд.
	 * <p> Для создания партии с произвольным числом людей и кубиков используйте
	 * {@link #DiceSession(int, int)}.
	 * 
	 * @see {@link #DiceSession(int, int)}
	 */
	public DiceSession() {
		nPlayers = 4;
		kDices = 5;
		assignPlayers();
		currentRound = new Round();
	}
	
	/**
	 * Конструктор класса {@code DiceSession}.
	 * Создаёт партию с заданным числом людей и кубиков. Автоматически создаётся первый раунд.
	 * <p> Конструктор без параметров создаёт партию с <b>nPlayers=4</b> и <b>kDices=5</b>.
	 * @param nPlayers		число людей в партии
	 * @param kDices		используемое число кубиков
	 * @see {@link #DiceSession()}
	 */
	public DiceSession(int nPlayers, int kDices) {
		this.nPlayers = nPlayers;
		this.kDices = kDices;
		assignPlayers();
		currentRound = new Round();
	}
	
	/**
	 * Класс, описывающий игроков в кости.
	 * @author Александр Борисов
	 */
	public class Player {
		//Переменные имени, результатов последнего броска,
		//суммы очков в последнем раунде и числа побед
		private String name;
		private int[] dices = new int[kDices];
		private int score;
		private int wins;
		
		//Конструктор класса.
		//Присваивает переданное в качестве аргумента имя объекту-игроку.
		//Не предназначен для вызова вне класса DiceSession
		private Player(String name) {
			this.name = name;
		}
		
		//Вспомогательный метод, описывающий изменение переменных игрока
		//при броске костей.
		//Не предназначен для непосредственного вызова вне класса DiceSession.
		//Для вызова извне используйте метод makeTurn() класса Round.
		private void turn() {
			for (int i = 0; i < kDices; i++) {
				int diceValue = rand.nextInt(N_DICE_FACES+1);
				dices[i] = diceValue;
				score += diceValue;
			}
		}
		
		/**
		 * Геттер-функция имени игрока.
		 * @return	Имя игрока.
		 */
		public String getName() {
			return this.name;
		}
		
		/**
		 * Геттер-функция результатов последнего броска.
		 * @return	Возвращает массив с результатами последнего броска.
		 */
		public int[] getDices() {
			return this.dices;
		}
		
		/**
		 * Геттер-функция числа очков игрока в последнем раунде.
		 * @return	Возвращает число очков игрока в последнем раунде.
		 */
		public int getScore() {
			return this.score;
		}
		
		/**
		 * Геттер-функция числа побед игрока в текущей партии.
		 * @return	Возвращает число побед игрока в текущей партии.
		 */
		public int getWins() {
			return this.wins;
		}
	}
	
	/**
	 * Класс, описывающий раунд игры в кости.
	 * @author Александр Борисов
	 */
	public class Round implements Leaderboard {
		//Переменная, хранящая игроков в том порядке,
		//в котором они должны ходить в данном раунде.
		private Player[] playerOrder = players.toArray(size -> new Player[size]);
		
		//Стэк с игроками, не сделавшими свой ход в раунде.
		private Stack<Player> remainingPlayers = new Stack<Player>();
		
		//Переменная, хранящая игрока, делающего свой ход.
		private Player currentPlayer;
		
		//Компаратор для ранжирования игроков по числу очков в данном раунде.
		//Используется при составлении Leaderboard (см. метод getLeaderboard()).
		Comparator<DiceSession.Player> scoreComparator = 
				new Comparator<DiceSession.Player>() {
			@Override
			public int compare(DiceSession.Player p1, DiceSession.Player p2) {
				return p2.getScore()-p1.getScore();
			}
		};
		
		/**
		 * Конструктор метода {@code Round()}.
		 */
		public Round() {
			if (nextPlayerOrder!=null) {
				playerOrder = nextPlayerOrder;
			}
			for (Player player : playerOrder) {
				player.score=0;
				remainingPlayers.push(player);
			}
		}
		
		/**
		 * Метод, описывающий один ход в течение данного раунда.
		 * Обновляет переменные игрока, сделавшего ход и определяет порядок ходов
		 * в следующем раунде.
		 */
		public void makeTurn() {
			currentPlayer = remainingPlayers.pop();
			currentPlayer.turn();
			if (!checkPlayersLeft()) {
				List<Player> temporary = Arrays.asList(getLeaderboard());
				Collections.reverse(temporary);
				nextPlayerOrder = temporary.toArray(size -> new Player[size]);
				getLeaderboard()[0].wins+=1;
			}
		}
		
		/**
		 * Геттер-функция для последнего игрока, сделавшего свой ход.
		 * @return	Возвращает последнего игрока, сделавшего свой ход.
		 */
		public Player getCurrentPlayer() {
			return currentPlayer;
		}
		
		/**
		 * Геттер-функция для Leaderboard.
		 * @return	Возвращает список игроков в порядке убывания очков за последний раунд.
		 */
		public Player[] getLeaderboard() {
			List<Player> leaderboard = new ArrayList<Player>(players);
			leaderboard.sort(scoreComparator);
			return leaderboard.toArray(size -> new Player[size]);
		}
		
		/**
		 * Метод, проверяющий, остались ли игроки, не сделавшие свой ход.
		 * @return	Возвращает булеву переменную,
		 * показывающую, остались ли игроки, не сделавшие свой ход.
		 */
		public boolean checkPlayersLeft() {
			if (remainingPlayers.size()==0) {
				return false;
			}
			else {
				return true;
			}
		}
	}
	
	/**
	 * Метод, начинающий новый раунд.
	 * <ul><li>	Если предыдущий раунд ещё не закончился, возвращает его же.
	 * <li>		Если партия закончилась, возвращает последний раунд.</ul>
	 * @return	Возвращает новый объект "раунд".
	 */
	public Round newRound() {
		if (isOver() || currentRound.checkPlayersLeft()) {
			return currentRound;
		}
		else  {
			currentRound = new Round();
			return currentRound;
		}
	}
	
	/**
	 * Геттер-функция для Leaderboard.
	 * @return	Возвращает список игроков в порядке убывания количества побед.
	 */
	public Player[] getLeaderboard() {
		List<Player> leaderboard = new ArrayList<Player>(players);
		leaderboard.sort(winsComparator);
		return leaderboard.toArray(size -> new Player[size]);
	}
	
	/**
	 * Геттер-функция для игроков.
	 * @return	Возвращает список игроков в том порядке, в котором они были созданы.
	 */
	public Player[] getPlayers() {
		return players.toArray(size -> new Player[size]);
	}
	
	/**
	 * Метод, проверяющий, закончилась ли партия.
	 * @return	Возвращает булеву переменную,
	 * показывающую, закончилась ли партия.
	 */
	public boolean isOver() {
		if (getLeaderboard()[0].wins<MAX_WINS) {
			return false;
		}
		else {
			return true;
		}
	}
}
