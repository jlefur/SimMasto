\name{regular.fraction}
\alias{regular.fraction}
\title{Construction de plans factoriels fractionnaires sym\'etriques entre les facteurs.}
\usage{regular.fraction(s, p, r, resolution)
}
\description{Construction de plans factoriels fractionnaires symétriques entre les facteurs.
Cette fonction permet de générer une fraction de résolution donnée pour \eqn{s}
facteurs à \eqn{p} modalités en \eqn{p^r} unités}
\value{liste à deux composantes, \code{plan} (le plan en base \eqn{p}) et
\code{matrice.cle} (la matrice clé contenant les relations de définition),
ou NULL si aucune solution n' été trouvée. Le plan est sous la forme d'une
matrice composée d'entiers modulo \eqn{p}}
\note{This is a simplified version of a more general library in preparation.
In this version, all factors must have the same prime number of levels
and only fractions with a given resolution can be constructed. The first
q factors are used as basic factors. The first solution is kept although
it may not be the most interesting one (no control of aberration). This
function is programmed entirely in R and so it is not efficient with respect
to computer time. There is no explicit check on the arguments and so it
is up to the user to restrict p to a prime number such as 2, 3, 5 or 7.}
\arguments{\item{s}{le nombre de facteurs}
\item{p}{un nombre entier premier égal au nombre de modalités par facteur}
\item{r}{un nombre entier définissant la taille du plan, égale à \eqn{p^r}}
\item{resolution}{la résolution de la fraction}
}
\examples{regular.fraction(s=8, p=2, r=4, resolution=4)
regular.fraction(s=9, p=2, r=4, resolution=4)}

