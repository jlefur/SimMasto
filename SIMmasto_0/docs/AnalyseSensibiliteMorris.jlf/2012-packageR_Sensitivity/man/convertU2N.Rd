\name{convertU2N}
\alias{convertU2N}
\title{Transformation d'un \'echantillon d'une loi uniforme vers une loi normale...}
\usage{convertU2N(x, param, couverture=0.95)
}
\description{Transformation d'un échantillon d'une loi uniforme vers une loi normale
de même moyenne et d'écart.type assurant que la loi uniforme couvre une
probabilité égale à l'argument couverture de la loi normale}
\arguments{\item{x}{échantillon d'une loi considérée uniforme}
\item{param}{ligne d'un data.frame de type xxx.factors}
\item{couverture}{une probabilité entre 0 et 1}
}
\examples{convertU2N(seq(8,10,length=11),fungus.factors["Topt",])}

