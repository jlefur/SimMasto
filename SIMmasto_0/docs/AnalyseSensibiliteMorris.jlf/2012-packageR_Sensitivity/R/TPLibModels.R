# ECOLE-CHERCHEURS MEXICO, GIENS 2012

# Fichier source des fonctions et structures associees aux modeles
# fungus, ishigami, weed et wwdm

#' Facteurs d'entrée du modèle "fungus" (croissance champignon selon l'humidité)
#' @docType data
#' @title Facteurs d'entrée de l'AS du modèle "fungus"
#' @return data.frame à 5 lignes (facteurs) et 4 colonnes (spécifs)
#' @note Les valeurs dans fungus.factors correspondent à Alternaria Brassicae,
#' ravageur du colza (Magarey et al, 2005)

fungus.factors <-
		structure(list(nominal = c(4.5, 9, 20, 4.5, 9.5),
                               binf = c(2.6, 8, 18, 4, 8),
                               bsup = c(6, 10, 22, 5, 11),
                               name = structure(c(2L, 3L, 1L, 5L, 4L),
                                 .Label = c("Tmax", "Tmin", "Topt", "Wmax", "Wmin"),
                                 class = "factor")),
                          .Names = c("nominal", "binf", "bsup", "name"),
                          row.names = c("Tmin", "Topt", "Tmax", "Wmin", "Wmax"),
                          class = "data.frame")

#' Modèle "fungus" (croissance champignon selon l'humidité)
#' @param param vecteur de longueur 5 comprenant un jeu de valeurs de Tmin, Topt, Tmax, Wmin, Wmax
#' @param temperature scalaire ou vecteur de temperatures
#' @note Des valeurs min, max et nominales des paramètres sont donnees
#'  dans fungus.factors. Elles correspondent à Alternaria
#'  Brassicae, ravageur du colza (Magarey et al, 2005).
#' @return scalaire ou vecteur de longueur egale au nombre de temperatures
#' @references Magarey RD, Sutton TB, Thayer CL (2005). A simple generic infection
#'  model for foliar fungal plant pathogens. Phytopathology 95, 92-100.
#' @examples  fungus.model( fungus.factors$nominal, temperature=c(10,15,18,21,30) )

fungus.model <-
function(param=fungus.factors$nominal, temperature=10)
  {
   if(class(param)=="numeric"){param <- as.data.frame(as.list(param))}
    names(param) = fungus.factors$name

    Tmin = param$Tmin
    Tmax= param$Tmax
    Topt=param$Topt
    Wmin=param$Wmin
    Wmax=param$Wmax


    FdeT=((Tmax - temperature)/(Tmax-Topt))*
      ((temperature-Tmin)/(Topt-Tmin))** ((Topt-Tmin)/(Tmax-Topt))


    W = Wmin/FdeT
    # On corrige lorsqu'on est en dehors du domaine
    W[temperature >= Tmax] = Wmax
    W[temperature <= Tmin ] = Wmax
    # Correction de base
    W[W > Wmax] = Wmax


    W
  }

#' Simulation du modèle "fungus"
#' @param X vecteur de longueur 5 ou matrice N x 5 comprenant un
#'          ou plusieurs jeux de valeurs de
#'          Tmin, Topt, Tmax, Wmin, Wmax
#' @param temperature scalaire ou vecteur de temperatures
#' @param tout TRUE si l'on veut les entrées ET les sorties dans le tableau de sortie
#' @note  Des valeurs min, max et nominales des paramètres sont donnees
#'  dans fungus.factors. Elles correspondent à Alternaria
#'  Brassicae, ravageur du colza (Magarey et al, 2005).
#' @return Data.frame a N lignes et p colonnes, ou p est la longueur de 'temperature'
#' @references Magarey RD, Sutton TB, Thayer CL (2005). A simple generic infection
#'  model for foliar fungal plant pathogens. Phytopathology 95, 92-100.
#' @examples
#' scenarios <- rbind(fungus.factors$binf,fungus.factors$nominal,fungus.factors$bsup)
#' fungus.simule( scenarios, temperature=c(10,15,18,21,30) )

fungus.simule <-
		function(X, temperature=10, tout=FALSE)
{
	if(is.null(dim(X))) X = matrix(X,nrow=1)

	res = t(apply(X,1,fungus.model,temperature=temperature))
	res=as.data.frame(res)
	names(res) <- paste("T.",temperature,sep="")

	if (tout) res  = cbind(X,res)
	return(res)
}

#' Facteurs d'entrée du modèle Ishigami, décrit dans \cite{Saltelli et al., 2000}
#' @docType data
#' @return data.frame à 3 lignes (facteurs) et 4 colonnes (spécifs)
#' @title Facteurs d'entrée du modèle Ishigami

ishigami.factors <-
structure(list(nominal = c(0, 0, 0), binf = c(-3.14159265358979,
-3.14159265358979, -3.14159265358979), bsup = c(3.14159265358979,
3.14159265358979, 3.14159265358979), name = structure(1:3, .Label = c("x1",
"x2", "x3"), class = "factor")), .Names = c("nominal", "binf",
"bsup", "name"), row.names = c("x1", "x2", "x3"), class = "data.frame")

#' Modèle d'Ishigami
#' @title Modèle d'Ishigami, décrit dans Saltelli et al., 2000
#' @param param vecteur de longueur 3 ou  matrice N x 3 des paramètres
#'          chacun des paramètres doit varier entre -pi et +pi
#' @return scalaire, ou vecteur de longueur N
#' @note Appelle la fonction ishigami.fun de la librairie sensitivity
#' @examples
#'  ishigami.model( c(-1,0,-1) )
#'  ishigami.model( rbind( c(1,1,1),c(-1,0,-1) )  )

ishigami.model <-
		function(param=ishigami.factors$nominal)
{
	if(length(param) == length(ishigami.factors$nominal) ){
		x = matrix(param,nrow=1)
	}
	else x <- param
	return(ishigami.fun(x))
}

#' Simulation du modèle d'Ishigami
#' @title Simulation du modèle d'Ishigami, décrit dans Saltelli et al., 2000
#' @param X matrice ou dataframe N x 3 des valeurs d'entrée, comprises entre -pi et +pi
#' @param tout TRUE si l'on veut les entrées ET les sorties dans le tableau de sortie
#' @return matrice ou dataframe si 'tout==TRUE', un vecteur sinon
#' @note Appelle la fonction ishigami.fun de la librairie sensitivity
#' @examples
#'  ishigami.simule( c(-1,0,-1) )
#'  ishigami.simule( rbind( c(1,1,1),c(-1,0,-1) )  )

ishigami.simule <-
		function(X, tout=FALSE)
{

	if(is.null(dim(X))) X = matrix(X,nrow=1)

	res = t(apply(X,1,ishigami.model))
	res = c(res)  #res=as.data.frame(res)
	if (tout) res  = cbind(X,res)
	return(res)
}

#' Dataframe des décisions par défaut pour le modèle "Weed"
#' @docType data
#' @return data.frame à 8 lignes (années) et 3 colonnes (facteurs)
#' @title Décisions par défaut pour le modèle "Weed"

weed.decision <-
structure(list(Soil.vec = c(1, 1, 1, 0, 1, 0, 1, 0), Crop.vec = c(1,
1, 1, 1, 1, 1, 0, 1), Herb.vec = c(1, 1, 1, 1, 1, 1, 1, 1)), .Names = c("Soil.vec",
"Crop.vec", "Herb.vec"), row.names = c(NA, -8L), class = "data.frame")

#' Facteurs d'entrée du modèle "Weed" (ou "mauvaises herbes")
#' @docType data
#' @return data.frame à 20 lignes (facteurs) et 4 colonnes (spécifs)
#' @title Facteurs d'entrée du modèle "Weed"

weed.factors <-
structure(list(nominal = c(0.84, 0.6, 0.55, 0.95, 0.2, 0.3, 0.05,
0.15, 0.3, 0.98, 0, 445, 296, 8, 0.002, 0.005, 400, 10000, 3350,
280), binf = c(0.756, 0.54, 0.495, 0.855, 0.18, 0.27, 0.045,
0.135, 0.27, 0.882, 0, 400.5, 266.4, 7.2, 0.0018, 0.0045, 300,
7500, 2512.5, 210), bsup = c(0.924, 0.66, 0.605, 1.045, 0.22,
0.33, 0.055, 0.165, 0.33, 1.078, 0.5, 489.5, 325.6, 8.8, 0.0022,
0.0055, 500, 12500, 4187.5, 350), name = structure(1:20, .Label = c("mu",
"v", "phi", "beta.1", "beta.0", "chsi.1", "chsi.0", "delta.new",
"delta.old", "mh", "mc", "Smax.1", "Smax.0", "Ymax", "rmax",
"gamma", "d", "S", "SSBa", "DSBa"), class = "factor"), continu = c(TRUE,
TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE,
TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE), initialisation = c(FALSE,
FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE,
FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, TRUE, TRUE, TRUE, TRUE
), info = c("", "", "", "", "", "", "", "", "", "", "", "", "",
"", "", "", "Seedling density (plants/m2)", "Seed production (grains/m2)",
"Surface seed bank (grains/m2)", "Deep seed bank (grains/m2)"
)), .Names = c("nominal", "binf", "bsup", "name", "continu",
"initialisation", "info"), row.names = c("mu", "v", "phi", "beta.1",
"beta.0", "chsi.1", "chsi.0", "delta.new", "delta.old", "mh",
"mc", "Smax.1", "Smax.0", "Ymax", "rmax", "gamma", "d", "S",
"SSBa", "DSBa"), class = "data.frame")

#' Fonction de base du modèle "Weed": calcul sur 1 année du modèle "Weed".
#' @title Fonction de base du modèle "Weed"
#' @param decision data.frame à 1 ligne et 3 colonnes Soil, Crop, Herb
#' @param param vecteur des paramètres:
#'             mu, v, phi, beta.1, beta.0, chsi.1, chsi.0,
#'             delta.new, delta.old, mh, mc, Smax.1, Smax.0,
#'             Ymax, rmax, gamma
#'          et des variables d'état initiales
#'             d.im1, S.im1, SSBa.im1, DSBa.im1 (d,S,SSBa,DSBa)
#' @return un vecteur de longueur 5 composé de:
#'  \itemize{
#'    \item{S}{production de graines par eqn(m^2)}
#'    \item{d}{densité d'adventices à l'émergence (plantes par eqn(m^2))}
#'    \item{SSBa}{banque de graines en surface après travail du sol (graines par eqn(m^2))}
#'    \item{DSBa}{banque de graines en profondeur après travail du sol (graines par eqn(m^2))}
#'    \item{Yield}{rendement (t par ha)}
#' }

weed.fun <-
		function(decision, param)
{
	p = as.data.frame( matrix(param,nrow=1))
	names(p) = weed.factors$name
	init = p[,weed.factors$initialisation]

	beta <- p$beta.1*decision$Soil + p$beta.0*(1-decision$Soil)
	chsi <- p$chsi.1*decision$Soil + p$chsi.0*(1-decision$Soil)
	Smax <- p$Smax.1*decision$Crop + p$Smax.0*(1-decision$Crop)
	alpha<- Smax/160000

	SSBb.i <- (1-p$mu)*(init$SSBa - init$d) + p$v*(1-p$phi)*init$S
	DSBb.i <- (1-p$mu)*init$DSBa

	SSBa.i <- (1-beta)*SSBb.i + chsi*DSBb.i
	DSBa.i <- (1-chsi)*DSBb.i + beta*SSBb.i

	d.i <- p$delta.new*p$v*(1-p$phi)*(1-beta)*init$S +
			p$delta.old*(SSBa.i-init$S*p$v*(1-p$phi)*(1-beta))
	D.i <- (1-p$mh*decision$Herb)*(1-p$mc)*d.i
	S.i <- Smax*D.i/(1+alpha*D.i)
	Yield.i <- p$Ymax*(1-(p$rmax*D.i/(1+p$gamma*D.i)))

	return(c(d.i, S.i, SSBa.i, DSBa.i, Yield.i))
}

#' Modèle "Weed" pour un jeu de paramètres et un jeu de décisions sur n années
#' @title Modèle "Weed" pour un jeu de paramètres et un jeu de décisions
#' @param param vecteur des paramètres:
#'             mu, v, phi, beta.1, beta.0, chsi.1, chsi.0,
#'             delta.new, delta.old, mh, mc, Smax.1, Smax.0,
#'             Ymax, rmax, gamma
#'          et des variables d'état initiales
#'             d.im1, S.im1, SSBa.im1, DSBa.im1 (d,S,SSBa,DSBa)
#' @param decision data.frame à 3 colonnes Soil, Crop, Herb de valeurs 0-1
#'        et n lignes, où n est le nombre d'années simulées
#' @param tout TRUE si l'on veut les entrées ET les sorties dans le tableau de sortie
#' @note Voir weed.factors pour les valeurs min, max et nominal des parametres
#' @return une matrice n x 5 composée de:
#'  \itemize{
#'    \item{S}{production de graines par eqn(m^2)}
#'    \item{d}{densité d'adventices à l'émergence (plantes par eqn(m^2))}
#'    \item{SSBa}{banque de graines en surface après travail du sol (graines par eqn(m^2))}
#'    \item{DSBa}{banque de graines en profondeur après travail du sol (graines par eqn(m^2))}
#'    \item{Yield}{rendement (t par ha)}
#' }
#' @examples
#'  decision <- data.frame(Soil=c(0,1),Crop=c(0,1),Herb=c(0,1))
#'  weed.model( weed.factors$nominal, decision=decision )

weed.model <-
		function(param, decision = weed.decision, tout = FALSE)
{
	init = param[weed.factors$initialisation==TRUE]
	NumY = nrow(decision)

	sortie = matrix(NA,nrow=NumY,ncol=5)
	dimnames(sortie) = list(paste("Annee",1:NumY,sep="."), c("d","S","SSBa","DSBa","Yield"))
	for(i in 1:NumY) {
		sortie[i,] = weed.fun(decision[i,],param)
		param[weed.factors$initialisation==TRUE] = sortie[i,1:4]
	}
	sortie
}

#' Simulations en série du modèle "Weed"
#' @title Simulations en série du modèle "Weed"
#' @param X matrice ou data.frame des jeux de paramètres:
#'             mu, v, phi, beta.1, beta.0, chsi.1, chsi.0,
#'             delta.new, delta.old, mh, mc, Smax.1, Smax.0,
#'             Ymax, rmax, gamma
#'          et des variables d'état initiales
#'             d.im1, S.im1, SSBa.im1, DSBa.im1 (d,S,SSBa,DSBa)
#' @param decision data.frame à 3 colonnes Soil, Crop, Herb et n
#'             lignes, où n est le nombre d'années simulées
#' @param sortie fonction ou mot-clé donnant la nature de la ou des variables
#'           en sortie de chaque simulation (voir DETAILS)
#' @param nom.sortie noms de la ou des variables de sortie retenues
#' @param tout TRUE si l'on veut les entrées ET les sorties dans le tableau de sortie
#' @note
#'  Le paramètre 'sortie' peut être:
#'  \itemize{
#'   \item soit une fonction calculant la ou les variables de sortie de chaque
#'     simulation à partir du tableau n x 5 des sorties de 'weed.model';
#'   \item soit un mot-cle pré-défini:
#'      \itemize{
#'       \item{annee.finale}{pour avoir les 5 sorties de la dernière année;}
#'       \item{rdt.total}{pour avoir la somme des rendements sur les n annees (defaut);}
#'       \item{banque.finale}{pour avoir la banque de graines en derniere annee.}
#' }}
#'  Pour rappel, les 5 sorties de 'weed.model' sont:
#'  \itemize{
#'    \item{S}{production de graines par eqn(m^2)}
#'    \item{d}{densité d'adventices à l'émergence (plantes par eqn(m^2))}
#'    \item{SSBa}{banque de graines en surface après travail du sol (graines par eqn(m^2))}
#'    \item{DSBa}{banque de graines en profondeur après travail du sol (graines par eqn(m^2))}
#'    \item{Yield}{rendement (t par ha)}
#' }
#' @return
#'  un data.frame incluant en colonnes la ou les sorties retenues. Suivant la
#'  valeur de 'tout', les entrées sont restituées ou non.
#' @examples
#'  jeux.parametres <- rbind(weed.factors$binf, weed.factors$nominal, weed.factors$bsup)
#'  weed.simule( jeux.parametres, sortie=function(x){sum(x[,5])}, nom.sortie="rdt.total")
#'  weed.simule( jeux.parametres, sortie="annee.finale", nom.sortie="rdt.total")

weed.simule <-
function(X, decision = weed.decision, sortie = "rdt.total", nom.sortie = NULL,
           tout = FALSE)
  {
    if(class(sortie)=="character"){
      if(sortie == "annee.finale"){
        sortie <- function(x){ x[nrow(x),] }
        nom.sortie <- c("d.FIN","S.FIN","SSBa.FIN","DSBa.FIN","Yield.FIN") }
      else if(sortie == "banque.finale"){
        sortie <- function(x){ sum(x[nrow(x),c(3,4)]) }
        nom.sortie <- c("BqGr") }
      else if(sortie == "rdt.total"){
        sortie <- function(x){ sum(x[,5]) }
        nom.sortie <- c("Y") }
    }
    res = t(apply(X, 1,
      function(v){ out <- weed.model(v,decision = decision) ;
                                    sortie(out) } ))
    if(length(res) != nrow(X)){
      res <- as.data.frame(res)
      names(res) = nom.sortie
    }
    else res <- c(res)

    if (tout) res  = cbind(X,res)
    return(res)
  }

#' Modèle wwdm (winter wheat dry matter) de croissance du blé,
#' modèle de culture très simple, dynamique à pas de temps journalier
#' @title Modèle "wwdm" pour un jeu de paramètres
#' @name wwdm.model
#' @param param vecteur de paramètres de wwdm de longueur 7 ou 8
#' @param year soit NULL soit un nombre compris entre 1 et 14
#' @param climate nom du data.frame contenant les données climatiques
#' @note
#'  Le modèle a deux variables d'état, l'indice de surface foliaire (LAI) et
#'  la biomasse aérienne du blé d'hiver. La sortie de la fonction est le gain de
#'  poids journalier de la matière sèche en 'g per m2 per day'.
#'  Une simulation correspond à une année climatique. Par défaut, les données
#'  climatiques sont lues dans le data.frame wwdm.climates, qui contient 14
#'  années climatiques, et l'année doit être spécifiée par un nombre entre 1 et 14.
#'  Il y a deux façons de faire cela, soit par l'argument \code{year}, soit
#'  par la 8ème coordonnée de l'argument \code{param} si \code{year=NULL}. Lorsque
#'  \code{year=NULL} et qu'il n'y a que 7 coordonnées dans \code{param}, l'année
#'  utilisée est l'année numéro 3
#' @return vecteur des 223 gains journaliers de biomasse calculés par WWDM
#' @references
#'   Makowski, D., Jeuffroy, M.-H., Guérif, M., 2004 Bayesian methods for
#'    updating crop model predictions, applications for predicting biomass and
#'    grain protein content. In: Bayesian Statistics and Quality Modelling in
#'    the Agro-Food Production Chain (van Boeakel et al. eds), pp. 57-68.
#'    Kluwer, Dordrecht.
#'
#'   Monod, H., Naud, C., Makowski, D., 2006 Uncertainty and sensitivity
#'    analysis for crop models. In: Working with Dynamic Crop Models (Wallach
#'    D., Makowski D. and Jones J. eds), pp. 55-100. Elsevier, Amsterdam
#' @examples
#'  #data()
#'  #wwdm.model()
#'  #sum( wwdm.model() )  #biomasse cumulee
#'  #wwdm.model(param=wwdm.factors$nominal, year=NULL, climate=wwdm.climates)
#'  #wwdm.model(param=wwdm.factors$nominal, year=5)

wwdm.model <-
    function(param, year=NULL, climate=wwdm.climates)
{

    if(class(param)=="numeric"){param <- as.data.frame(as.list(param))}
    names(param) = wwdm.factors$name[seq(length(param))]

    Eb    <- param$Eb
    Eimax <- param$Eimax
    K     <- param$K
    Lmax  <- param$Lmax
    A     <- param$A
    B     <- param$B
    TI    <- param$TI
    if (is.null(year)){
        if(ncol(param) == 8) year <- param$Clim
        else year <- 3
    }
    ## Calcul de PAR et de ST	 à partir des fichiers climatiques
    PAR <- 0.5*0.01*climate$RG[climate$ANNEE==year]
    Tmean <- (climate$Tmin[climate$ANNEE==year]+climate$Tmax[climate$ANNEE==year])/2
    Tmean[Tmean<0] <- 0
    ST <- Tmean
    for (i in (2:length(Tmean)))
    {
        ST[i] <- ST[i-1]+Tmean[i]
    }

    ## Calcul de LAI
    Tr <- (1/B)*log(1+exp(A*TI))
    LAI <- Lmax*((1/(1+exp(-A*(ST-TI))))-exp(B*(ST-(Tr))))
    LAI[LAI<0] <- 0

    ## Calcul de la biomasse (g/m2)
    U <- Eb*Eimax*(1-exp(-K*LAI))*PAR
    ## BIOMASSE <-sum(U)

    return(U)
}

#' Fonction gérant une série de simulations de wwdm, modèle de culture dynamique
#' à pas de temps journalier, pour le blé
#' @title Simulations en série du modèle "wwdm"
#' @name wwdm.simule
#' @param X dataframe à 7 ou 8 colonnes de valeurs des paramètres de wwdm
#' @param year soit NULL, soit une valeur unique entre 1 et 14
#' @param tout TRUE si l'on veut les entrées ET les sorties dans le tableau de sortie
#' @param transfo TRUE si X contient des valeurs codées entre 0 et 1
#' @param b1 vecteur des 7 ou 8 bornes inférieures des paramètres si transfo=TRUE
#' @param b2 vecteur des 7 ou 8 bornes supérieures des paramètres si transfo=TRUE
#' @note
#'  Le modèle a deux variables d'état, l'indice de surface foliaire (LAI) et
#'  la biomasse aérienne du blé d'hiver. La fonction wwdm.simule ne donne
#'  en sortie que la biomasse aérienne accumulée avant la récolte, en 'g per m2'.
#'  Une simulation correspond à une année climatique. Il est possible de
#'  préciser l'année climatique, soit simulation par simulation en ajoutant une
#'  colonne 'year' à 'X', soit globalement en utilisant l'argument 'year'
#' @return
#'  Biomasse aérienne accumulée avant la récolte, en g per m2
#' @references
#'   Makowski, D., Jeuffroy, M.-H., Guérif, M., 2004 Bayseian methods for
#'    updating crop model predictions, applications for predicting biomass and
#'    grain protein content. In: Bayseian Statistics and Quality Modelling in
#'    the Agro-Food Production Chain (van Boeakel et al. eds), pp. 57-68.
#'    Kluwer, Dordrecht
#'
#'   Monod, H., Naud, C., Makowski, D., 2006 Uncertainty and sensitivity
#'    analysis for crop models. In: Working with Dynamic Crop Models (Wallach
#'    D., Makowski D. and Jones J. eds), pp. 55-100. Elsevier, Amsterdam
#' @examples
#'  jeux.parametres <- as.data.frame(rbind(wwdm.factors$binf,wwdm.factors$nominal, wwdm.factors$bsup))
#'  names(jeux.parametres) <- wwdm.factors$name
#'  wwdm.simule(jeux.parametres)

wwdm.simule <- function(X, year=NULL, tout=FALSE,
                        transfo = FALSE,
                        b1=wwdm.factors$binf[1:Nbfac],
                        b2=wwdm.factors$bsup[1:Nbfac]){
#  à utiliser pour analyser  la FC wwdm  avec morris() de sensitivity
#  transfo = T : recodage de la matrice X codée dans [0,1]
#  binf = vecteur des bornes inférieures des gammes des facteurs
#  bsup = vecteur des bornes supérieures des gammes des facteurs
  if(transfo){
      Nbfac <- ncol(X)
      X <- t(b1 + t(X)*(b2-b1))
  }
  if(is.null(year))
      sortie <- apply(X,1, function(v) sum(wwdm.model(v[1:7],v[8])) )
   else
      sortie <- apply(X,1,function(v) sum(wwdm.model(v[1:7],year=year)))

  if(tout) sortie = cbind(X,Biomasse = sortie)

  return(sortie)
}

#' Séries climatiques sur 14 années, utilisées par le modèle wwdm
#' @title Séries climatiques sur 14 années, utilisées par le modèle wwdm
#' @docType data
#' @name wwdm.climates
#' @return data.frame à N lignes (1 par jour) et 4 colonnes (ANNEE, RG, Tmin, Tmax)

NULL # Voir dans le répertoire data

#' Facteurs d'entrée du modèle "wwdm"
#' @docType data
#' @title Facteurs d'entrée du modèle "wwdm"
#' @return data.frame à 8 lignes (facteurs) et 4 colonnes (spécifs)

wwdm.factors <-
    structure(list(nominal = c(1.85, 0.94, 0.7, 7.5, 0.0065, 0.00205,
                   900, 3),
                   binf = c(0.9, 0.9, 0.6, 3, 0.0035, 0.0011, 700, 1),
                   bsup = c(2.8, 0.99, 0.8, 12, 0.01, 0.0025, 1100, 14),
                   name = c("Eb", "Eimax", "K", "Lmax", "A", "B", "TI", "Clim"),
                   continu = c(TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, TRUE, FALSE)),
              .Names = c("nominal", "binf", "bsup", "name", "continu"),
              row.names = c("Eb", "Eimax", "K", "Lmax", "A", "B", "TI", "Clim"),
              class = "data.frame")

