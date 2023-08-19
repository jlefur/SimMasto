\name{samplingOptLHS}
\alias{samplingOptLHS}
\title{Tirage aleatoire d'un plan LHS optimal de N jeux de parametres...}
\usage{samplingOptLHS(dim_x, nom=c(NA), N=1, lois=rep(0, dim_x), 
    paramlois=array(0, dim =c(4, dim_x)), tronq=rep(FALSE, dim_x),
    paramtronq=array(0, dim =c(2, dim_x)), 
    optimal="maximin", dup=1, pop=100, gen=4, pMut=0.1)
}
\description{Tirage aleatoire d'un plan LHS optimal de N jeux de parametres
Le plan peut etre maximin, distance-optimal ou S-optimal}
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

library(lhs)

library(evd) # Gumbel

*********************************************************************}
\arguments{\item{dim_x}{nombre de parametres d'entree dans le modele}
\item{nom}{vecteur des noms des parametres simules
(defini comme : \code{c("V1","V2","V3",...)})}
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
\item{tronq}{TODO}
\item{paramtronq}{TODO}
\item{optimal}{type d'optimalite pour le LHS (par defaut 'maximin')
'distance' pour distance-optimal, 'S' pour S-optimal}
\item{dup}{facteur pour le nb de points candidats dans les fcts
maximinLHS (plan maximin) et improvedLHS (plan dist-optimal)}
\item{pop}{option de la fct geneticLHS (plan S-optimal)
Taper help(geneticLHS) pour en savoir plus}
\item{gen}{option de la fct geneticLHS (plan S-optimal)
Taper help(geneticLHS) pour en savoir plus}
\item{pMut}{option de la fct geneticLHS (plan S-optimal)
Taper help(geneticLHS) pour en savoir plus}
}

