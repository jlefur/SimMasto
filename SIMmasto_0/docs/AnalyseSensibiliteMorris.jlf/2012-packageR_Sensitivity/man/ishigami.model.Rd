\name{ishigami.model}
\alias{ishigami.model}
\title{Mod\`ele d'Ishigami, d\'ecrit dans Saltelli et al., 2000}
\usage{ishigami.model(param=ishigami.factors$nominal)
}
\description{Modèle d'Ishigami}
\value{scalaire, ou vecteur de longueur N}
\note{Appelle la fonction ishigami.fun de la librairie sensitivity}
\arguments{\item{param}{vecteur de longueur 3 ou  matrice N x 3 des paramètres
chacun des paramètres doit varier entre -pi et +pi}
}
\examples{ishigami.model( c(-1,0,-1) )
ishigami.model( rbind( c(1,1,1),c(-1,0,-1) )  )}

