\name{samplingSimple}
\alias{samplingSimple}
\title{Tirage aleatoire simple de N jeux de parametres...}
\usage{samplingSimple(dim_x, nom=c(NA), N=1, lois=rep(0, dim_x), 
    paramlois=array(0, dim = c(4, dim_x)), tronq=rep(FALSE, dim_x), 
    paramtronq=array(0, dim =c(2, dim_x)))
}
\description{Tirage aleatoire simple de N jeux de parametres}
\value{la matrice des N simulations des \code{dim_x} parametres}
\author{B. Iooss}
\note{!!! WARNING !!!! : la troncature ne s'applique pas a la loi trapezoidale

*********************************************************************

FONCTION UTILISEE :

truncated.R (fonctions pour lois tronquees)

*********************************************************************

LIBRAIRIES REQUISES (A INSTALLER)

library(triangle)

library(evd) # Gumbel

*********************************************************************}
\arguments{\item{dim_x}{nombre de parametres d'entree dans le modele}
\item{nom}{: vecteur des noms des parametres simules
(defini comme : c("V1","V2","V3",...))}
\item{N}{nombre de jeux de simulations}
\item{lois}{vecteur contenant les types de distribution de proba
pour chaque entree
0=uniforme ; 1=normale ; 2=lognormale ; 3=weibull
4=exponentielle ; 5=beta ; 6=triangulaire ; 7=trapezoidale
10=gumbel
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
\item{tronq}{vecteur pour selectionner ou non une loi tronquee
TRUE pour loi tronquee, FALSE sinon (par défaut)}
\item{paramtronq}{tableau avec les parametres de troncature de
chaque loi : (min,max) range par colonne}
}

