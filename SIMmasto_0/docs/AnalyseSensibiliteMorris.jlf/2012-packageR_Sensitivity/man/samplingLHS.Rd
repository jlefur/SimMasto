\name{samplingLHS}
\alias{samplingLHS}
\title{Tirage al\'eatoire LHS de N jeux de param\`etres...}
\usage{samplingLHS(dim_x, nom=c(NA), N=1, lois=rep(0, dim_x),
    paramlois=array(0, dim =c(4, dim_x)), 
    correl=0, tronq=rep(FALSE, dim_x),
    paramtronq=array(0, dim = c(2, dim_x)))
}
\description{Tirage aléatoire LHS de N jeux de paramètres
(possibilite d'imposer une matrice de corrélations sur les rangs)}
\references{Stein, M. 1987. Technometrics 29:143-151

Iman and Conover. 1982. Commun. Stat. Simul. Comput. 11(3):311-334

McKay, Conover and Beckman. 1979. Technometrics 21: 239-245}
\value{la matrice des N simulations des \code{dim_x} parametres}
\author{B. Iooss}
\note{!!! WARNING !!!! : le LHS et la la troncature ne s'appliquent pas a la
loi trapezoidale

*********************************************************************

FONCTION UTILISEE :

truncated.R (fonctions pour lois tronquees)

*********************************************************************

LIBRAIRIES REQUISES (A INSTALLER)

library(triangle)

library(evd) # Gumbel}
\arguments{\item{dim_x}{nombre de paramètres d'entrée dans le modèle}
\item{nom}{vecteur des noms des parametres simules
(defini comme : c("V1","V2","V3",...))}
\item{N}{nombre de jeux de simulations}
\item{lois}{vecteur contenant les types de distribution de proba
pour chaque entrée:
0=uniforme ; 1=normale ; 2=lognormale ; 3=weibull
4=exponentielle ; 5=beta ; 6=triangulaire ; 7=trapezoidale
10=gumbel.
Par defaut, on prend la loi uniforme}
\item{paramlois}{tableau avec les parametres de chaque loi (max=4)
pour chaque entree (range par colonne) :
(min,max,0,0) pour uniforme (par défaut : min=0, max=1)
(moy,ecart-type,0,0) pour normale,
(moy du log, ecart-type du log,0,0) pour lognormale,
(forme,echelle,0,0) pour Weibull,
(lambda,0,0,0) pour exponentielle,
(shape1, shape2,0,0) pour beta,
(min,mode,max,0) pour triangulaire,
(min,mode1,mode2,max) pour trapezoidale,
(mode,echelle,0,0) pour Gumbel}
\item{correl}{0 \eqn{\rightarrow} pas de correlation entre parametres;
1 \eqn{\rightarrow} pas de correlation, on supprime les correlations
indesirables par la methode des permutations circulaires
2 \eqn{\rightarrow} introduction d'une matrice de correlations sur
les rangs des parametres via le fichier 'matcorrelrank.dat'}
\item{tronq}{vecteur pour sélectionner ou non une loi tronquée: TRUE pour loi tronquée, FALSE sinon}
\item{paramtronq}{tableau avec les paramètres de troncature de chaque loi: (min,max) range par colonne}
}

