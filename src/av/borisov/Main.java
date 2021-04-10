package av.borisov;

public class Main {
	
	//Метод, выводящий на экран Leaderboard по количеству побед в партии.
	public static void printLeaderboard(DiceSession session) {
		DiceSession.Player[] leaderboard = session.getLeaderboard();
		System.out.println("Имя"+"		"+"Число побед");
		for (DiceSession.Player player : leaderboard) {
			System.out.println(player.getName()+"		"+player.getWins());
		}
	}
	
	//Метод, выводящий на экран Leaderboard по сумме очков в раунде.
	public static void printLeaderboard(DiceSession.Round round) {
		DiceSession.Player[] leaderboard = round.getLeaderboard();
		System.out.println("Имя"+"		"+"Число очков");
		for (DiceSession.Player player : leaderboard) {
			System.out.println(player.getName()+"		"+player.getScore());
		}
	}
	
	//Метод, выводящий на экран результат хода игрока.
	public static void printDices(int[] dices) {
		for (int dice : dices) {
			System.out.print(dice+" ");
		}
	}
	
	//Метод, выводящий на экран информацию о текущем ходе.
	public static void printRoundInfo(DiceSession.Round round) {
		System.out.println("Ходит игрок "+round.getCurrentPlayer().getName());
		printDices(round.getCurrentPlayer().getDices());
		System.out.println();
		System.out.println("Сумма очков: "+round.getCurrentPlayer().getScore()+"\n");
		System.out.println();
	}
	
	//Метод, выводящий на экрн информацию о результатах раунда.
	public static void printRoundResults(DiceSession session, DiceSession.Round round) {
		System.out.println("Раунд окончен. Победитель: "+round.getLeaderboard()[0].getName()+"\n");
		printLeaderboard(round);
		System.out.println("\n");
		System.out.println("Промежуточные результаты:\n");
		printLeaderboard(session);
		System.out.println("\n\n\n\n\n\n");
	}

	public static void main(String[] args) {
		
		//Реализация партии игры в кости, через класс DiceSession.
		DiceSession session = new DiceSession();
		DiceSession.Round round;
		int i=1;
		while (!session.isOver()) {
			System.out.println("Раунд "+i+" начался\n");
			round = session.newRound();
			while (round.checkPlayersLeft()) {
				round.makeTurn();
				printRoundInfo(round);
			}
			i+=1;
			printRoundResults(session, round);
		}
		
		System.out.println("Игра окончена. Общее число раундов: "+i+"\n");
		printLeaderboard(session);
	}

}