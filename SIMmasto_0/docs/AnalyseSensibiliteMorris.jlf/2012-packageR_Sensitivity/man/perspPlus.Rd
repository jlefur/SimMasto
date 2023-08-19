\name{perspPlus}
\alias{perspPlus}
\title{Interface conviviale pour des graphiques pour 3 variables...}
\usage{perspPlus(x, y, z, pcol=c("blue", "green"), pphi=30, ptheta=-30,
    nomx=deparse(substitute(x)), nomy=deparse(substitute(y)),
    nomz=deparse(substitute(z)), type=1)
}
\description{Interface conviviale pour des graphiques pour 3 variables}
\value{invisible()}
\arguments{\item{x}{voir la doc de persp}
\item{y}{voir la doc de persp}
\item{z}{voir la doc de persp}
\item{pcol}{code pour le dégradé de couleurs}
\item{pphi}{angle de vue (colatitude, argument phi de persp)}
\item{ptheta}{angle de vue (direction azimutale, argument theta de persp)}
\item{nomx}{chaine de caractère}
\item{nomy}{chaine de caractère}
\item{nomz}{chaine de caractère}
\item{type}{un chiffre. 1: perspective; 2: image; 3: contour; 4: perspective 3D}
}
\examples{#perspPlus(x=Tmax,y=Tmin,z = Y10, pcol=c("blue", "green"), pphi=30, ptheta=-30,
#     nomx=deparse(substitute(x)),nomy=deparse(substitute(y)),
#     nomz=deparse(substitute(z)),type=1)}

