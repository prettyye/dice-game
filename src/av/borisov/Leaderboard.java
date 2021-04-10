package av.borisov;

/**
 * Обязывает возвращать ранжированный список игроков в тех классах,
 * где данный интерфейс реализован.
 * @author Александр Борисов
 */
public interface Leaderboard {
	/**
	 * Метод, возвращающий упорядоченный список игроков
	 * @return	Возвращает массив объектов {@code DiceSession.Player} в некотором порядке.
	 */
	DiceSession.Player[] getLeaderboard();
}