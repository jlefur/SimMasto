\name{ishigami.simule}
\alias{ishigami.simule}
\title{Simulation du mod\`ele d'Ishigami, d\'ecrit dans Saltelli et al., 2000}
\usage{ishigami.simule(X, tout=FALSE)
}
\description{Simulation du modèle d'Ishigami}
\value{matrice ou dataframe si 'tout==TRUE', un vecteur sinon}
\note{Appelle la fonction ishigami.fun de la librairie sensitivity}
\arguments{\item{X}{matrice ou dataframe N x 3 des valeurs d'entrée, comprises entre -pi et +pi}
\item{tout}{TRUE si l'on veut les entrées ET les sorties dans le tableau de sortie}
}
\examples{ishigami.simule( c(-1,0,-1) )
ishigami.simule( rbind( c(1,1,1),c(-1,0,-1) )  )}

