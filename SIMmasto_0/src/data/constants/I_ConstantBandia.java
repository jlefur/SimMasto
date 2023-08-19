package data.constants;

public interface I_ConstantBandia extends I_ConstantNumeric {

	// la taille de la grille n'est pas conforme, il y a à peu près 130 mètres de chaque côté. J'ai joué sur la taille d'une
	// cellule et les intervalles entre pièges (sachant que c'est transformé en int pour la grille et donc on a peu de marge de
	// manoeuvre pour régler finement. Il faut modifier la taille du domaine et refaire la digitalisation. Jean Le Fur 09.08.2013

	public static final int TRAP_LINES = 19;//
	public static final int TRAP_COLS = 15;
	public static final double TRAP_INTERVALx_Umeter = 8.;
	public static final double TRAP_INTERVALy_Umeter = 7;
	public static final int TRAP0_x_Ucell = 67;
	public static final int TRAP0_y_Ucell = 63;
	public static final int TRAP_MAX_LOAD = 3;
	public static final int TRAP_AFFINITY = 10;
	public static final double TRAP_LOADING_PROBA = 0.9;

	// For BandiaEvents.csv : The chrono

	public static final String CHRONO_FILENAME = "20131209_BandiaEvents.2a.csv";
}
