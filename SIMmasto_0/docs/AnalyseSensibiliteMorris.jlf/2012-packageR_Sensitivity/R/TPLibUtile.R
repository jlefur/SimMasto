# ECOLE-CHERCHEUR MEXICO, GIENS 2012
# Fichier source des fonctions sur les methodes

#' Projection de valeurs tirées entre 0 et 1 sur une autre plage de variation
#' @param matrice matrice ou data.frame 0-1 à \eqn{N} lignes et \eqn{p} colonnes
#' @param minAmax vecteur des bornes de l'intervalle cible
#' @return matrice
#' @note Utilisé dans lhs.plan.
#' @examples \dontrun{TODO}

lhs2intervalle <-
    function(matrice, minAmax)
{
    mat = matrix( rep(minAmax$binf,rep(nrow(matrice),ncol(matrice))),nrow=nrow(matrice))
    mat = mat +as.matrix( matrice)%*% diag(minAmax$bsup - minAmax$binf)
    dimnames(mat)[[2]] = minAmax$name
    mat
}

#' Tire selon le plan hyper-cube latin un échantillon de valeurs de paramètres
#' @param taille de l'échantillon
#' @param plage objet de type .factors
#' @param repet NULL ou une ligne d'un objet de type .factors
#' @param tout TRUE si l'on veut conserver l'échantillon de base dans \eqn{[0,1]^p}
#' @return la matrice \eqn{N \times p} de l'échantillon si \code{tout=FALSE}
#'  ou une liste à 2 composantes, plan et lhs.tirage, si \code{tout=TRUE}
#' @note L'objet .factors donné dans l'argument plage sert à spécifier le nom
#'  des facteurs et les bornes de leurs intervalles d'incertitude.
#'  L'argument repet permet d'inclure un facteur supplémentaire qualitatif, dont les
#'  modalités sont obtenues par des tirages aléatoires indépendants avec remise.
#' @examples
#'   lhs.plan( taille = 10, plage = fungus.factors, repet = NULL, tout = FALSE)
#'   lhs.plan( taille = 10, plage = wwdm.factors[1:7,], repet = wwdm.factors[8,], tout = FALSE)

lhs.plan <-
    function( taille, plage, repet = NULL, tout = FALSE)
{

    plan =  randomLHS(taille, nrow(plage))

    #
    ## Si binf=bsup, cela va marcher mais on va perdre en qualite de plan
    #
    #
    tirage.lhs = as.data.frame(plan)
    names(tirage.lhs) = plage$name

    plan=lhs2intervalle(plan,plage[, c("binf","bsup")])
    plan = as.data.frame(plan)
    names(plan) = plage$name

    #
    ## Cas particulier pour meteo sans une serie$
    ## Ce n'est pas generalisable a tout directement car
    ## cela depend de la loi de tirage.... et bien d'autre chose
    #

    if(!is.null(repet)) {
        rep= sample(seq(repet[,"binf"],repet[,"bsup"]),taille,replace=TRUE)
        plan = cbind(plan,rep)
        names(plan) = c(plage$name,repet[,"name"])
    }

    if(tout) retour = list(plan=plan,tirage.lhs=tirage.lhs) else retour = plan
    return(retour)
}

#' Interface conviviale pour des graphiques pour 3 variables
#' @param x voir la doc de persp
#' @param y voir la doc de persp
#' @param z voir la doc de persp
#' @param pcol code pour le dégradé de couleurs
#' @param pphi angle de vue (colatitude, argument phi de persp)
#' @param ptheta angle de vue (direction azimutale, argument theta de persp)
#' @param nomx chaine de caractère
#' @param nomy chaine de caractère
#' @param nomz chaine de caractère
#' @param type un chiffre. 1: perspective; 2: image; 3: contour; 4: perspective 3D
#' @return invisible()
#' @examples
#'  #perspPlus(x=Tmax,y=Tmin,z = Y10, pcol=c("blue", "green"), pphi=30, ptheta=-30,
#'  #     nomx=deparse(substitute(x)),nomy=deparse(substitute(y)),nomz=deparse(substitute(z)),
#'  #     type=1)

perspPlus <-
    function(x, y, z, pcol=c("blue", "green"), pphi=30, ptheta=-30,
             nomx=deparse(substitute(x)),nomy=deparse(substitute(y)),nomz=deparse(substitute(z)),
             type=1)
{
    #
    # type = Graphique (1) par defaut  perspective, (2) image, (3) contour, (4) en 3dimension
    #
    if(is.matrix(z)) {
        xx=x
        yy=y
        zz=z
    }
    else
    {
        toto = interp(x,y,z)
        xx=toto$x
        yy=toto$y
        zz=toto$z
    }

    nrz <- nrow(zz)
    ncz <- ncol(zz)
    # Create a function interpolating colors in the range of specified colors
    jet.colors <- colorRampPalette(pcol )
    # Generate the desired number of colors from this palette
    nbcol <- 100
    color <- jet.colors(nbcol)
    # Compute the z-value at the facet centres
    zfacet <- zz[-1, -1] + zz[-1, -ncz] + zz[-nrz, -1] + zz[-nrz, -ncz]
    # Recode facet z-values into color indices
    facetcol <- cut(zfacet, nbcol)
    if(type==1)
        persp(xx, yy, zz, col=color[facetcol], phi=pphi, theta=ptheta,xlab=nomx,ylab=nomy,zlab=nomz)
    if(type==2)
        image(xx, yy, zz, col=color[facetcol], xlab=nomx,ylab=nomy,main=nomz)
    if(type==3)
        contour(xx, yy, zz, col=color[facetcol], xlab=nomx,ylab=nomy,main=nomz)
    if(type==4)
        persp3d(xx, yy, zz, col=color[facetcol], xlab=nomx,ylab=nomy,main=nomz, zlab=nomz)

    invisible()
}

### CONSTRUCTIONS DE PLANS FRACTIONNAIRES DE BASE ###

#' Utilitaire plans fractionnaires: conversion base p vers base 10
#' @param x matrice dont les lignes forment des nombres en base p
#' @param p un nombre entier premier
#' @return vecteur des valeurs en base 10
#' @note les coefficients sont supposés ordonnés par puissances croissantes de p
#' @examples
#'  conv.into2 <- convertinto.basep( x=c(0:16), p=2 )
#'  convertfrom.basep(x=conv.into2, p=2)

convertfrom.basep <-
    function (x, p)
{
    ## Conversion of integers x coded as vectors of coefficients in base p
    ## to classical integers in base 10

    if (!is.numeric(x))
        stop("cannot recompose non-numeric arguments")
    if( (max(x)>p) || (min(x)<0) )
        stop("x must be reduced modulo p")
    if (is.matrix(x)) {
        l <- rep(NA, nrow(x))
        for(i in seq(along = l)){
            l[i] <- Recall(x[i,],p)
        }
        return(l)
    }
    val <- sum( x * p^(seq(along=x)-1) )
    return(val)
}

#' Utilitaire plans fractionnaires: conversion base 10 vers base p
#' @param x vecteur des valeurs en base 10
#' @param p un nombre entier premier
#' @return matrice dont les lignes forment des nombres en base p
#' @note les coefficients sont ordonnés par puissances croissantes de p
#' @examples
#'  convertinto.basep( x=c(0:16), p=2 )
#'  convertinto.basep( x=c(0:16), p=3 )

convertinto.basep <-
    function (x, p)
{
  ## Conversion of an integer or integer vector x into base p
  ## The coefficients are ordered by increasing powers of p
    if (!is.numeric(x))
        stop("cannot decompose non-numeric arguments")
    if (length(x) > 1) {
        l <- matrix(0, length(x), length(Recall(max(x),p)))
        for(i in seq(along = x)){
            dec.i <- Recall(x[i],p)
            l[i, seq(along=dec.i) ] <- dec.i
        }
        return(l)
    }
    if (x != round(x) || x < 0)
        return(x)
    val <- x%%p
    while ( (x <- x%/%p) > 0 ) {
        newval <- x%%p
        val <- c(val,newval)
    }
    return(val)
}

#' Génération d'un plan factoriel complet à partir des nombres de modalités de
#' \eqn{s} facteurs
#' @param n vecteur des nombres de modalités
#' @param start valeur du 1er chiffre utilisé pour les modalités des facteurs
#' @return matrice à 1 ligne par combinaison et 1 colonne par facteur
#' @examples
#'  crossing( c(2,3,4), start=0 )

crossing <-
    function(n,start=1)
{
    ## Generates all n1 x n2 x ... x ns combinations of size s with n1,...,ns integers

    N <- prod(n)
    s <- length(n)
    n <- c(n,1)
    crosses <- matrix(NA, N, s)
    for(i in seq(s))
    {
        motif <- start + seq(n[s+1-i])-1
        repet1 <- rep( prod(n[s+1-i+seq(i)]), n[s+1-i] )
        if(i==s){ repet2 <- 1 }
        else{ repet2 <- prod(n[seq(s-i)]) }
        crosses[,s-i+1] <- rep( rep( motif, repet1 ), repet2 )
    }
    return(crosses)
}

#' Calcul basique des inverses modulo \eqn{p}
#' @param p un nombre entier premier
#' @return vecteur des inverses
#' @examples
#'  inverses.basep(5)
#'  (inverses.basep(17) * (1:16)) \%\%17

inverses.basep <-
    function(p)
{
    ## Raw calculation of the inverses modulo p

    if(p==2) return(1)
    else if(p==3) return(c(1,2))
    products <- outer(seq(2,p-2), seq(2,p-2), "*")%%p
    inverses <- 1 + apply(products, 1, function(x){ seq(along=x)[x==1] })
    return( c(1,inverses,p-1) )
}

#' Vérification des confusions d'effets en cours de construction
#' d'un plan factoriel fractionnaire
#' @param PhiStar matrice clé en cours
#' @param admissible matrice codant les caractères a priori admissibles
#' @param IneligibleSet ensemble des caractères non éligibles
#' @param p un nombre entier premier
#' @return vecteur logique

planor.kernelcheck.basep <-
    function(PhiStar, admissible, IneligibleSet, p)
{
    ImagesIS <- (- PhiStar %*% IneligibleSet)%%p
    avoid <- convertfrom.basep( t(ImagesIS), p)
    candidate <- convertfrom.basep( t(admissible), p)
    test <- !(candidate %in% avoid)
    return(test)
}

#' Construction de plans factoriels fractionnaires symétriques entre les facteurs.
#' Cette fonction permet de générer une fraction de résolution donnée pour \eqn{s}
#' facteurs à \eqn{p} modalités en \eqn{p^r} unités
#' @param s le nombre de facteurs
#' @param p un nombre entier premier égal au nombre de modalités par facteur
#' @param r un nombre entier définissant la taille du plan, égale à \eqn{p^r}
#' @param resolution la résolution de la fraction
#' @return liste à deux composantes, \code{plan} (le plan en base \eqn{p}) et
#'  \code{matrice.cle} (la matrice clé contenant les relations de définition),
#'  ou NULL si aucune solution n' été trouvée. Le plan est sous la forme d'une
#'  matrice composée d'entiers modulo \eqn{p}
#' @note
#'  This is a simplified version of a more general library in preparation.
#'  In this version, all factors must have the same prime number of levels
#'  and only fractions with a given resolution can be constructed. The first
#'  q factors are used as basic factors. The first solution is kept although
#'  it may not be the most interesting one (no control of aberration). This
#'  function is programmed entirely in R and so it is not efficient with respect
#'  to computer time. There is no explicit check on the arguments and so it
#'  is up to the user to restrict p to a prime number such as 2, 3, 5 or 7.
#' @examples
#'  regular.fraction(s=8, p=2, r=4, resolution=4)
#'  regular.fraction(s=9, p=2, r=4, resolution=4)

regular.fraction <-
    function(s,p,r,resolution)
{
    ## DESCRIPTION
    ##  generates a regular fractional factorial design of given resolution,
    ##  for s factors at p levels in p^r units
    ## ARGUMENTS
    ##  s : number of input factors
    ##  p : unique prime number of levels of all input and unit factors
    ##  r : number of unit factors (so that there are N=p^r units)
    ##  resolution : resolution of the fraction
    ## DETAILS
    ##  This is a simplified version of a more general library in preparation.
    ##  In this version, all factors must have the same prime number of levels
    ##  and only fractions with a given resolution can be constructed. The first
    ##  q factors are used as basic factors. The first solution is kept although
    ##  it may not be the most interesting one (no control of aberration). This
    ##  function is programmed entirely in R and so it is not efficient with respect
    ##  to computer time. There is no explicit check on the arguments and so it
    ##  is up to the user to restrict p to a prime number such as 2, 3, 5 or 7.
    ## OUTPUT:
    ##  a list with two components: plan (the design in base p) and matrice.cle
    ##  (the design key). The design has N=p^r rows (units) and s columns (factors).
    ##  All its elements are integers modulo p that represent the factor levels.

                                        # ensemble ineligible
    cat("Determination des termes ineligibles: ")
    ineligible <- diag(s)
    for(reso in 2:(resolution-1)){
        combis <- combn(s,reso)
        ncombi <- ncol(combis)
        select <- cbind( c(combis), rep(seq(ncombi),rep(reso,ncombi)) )
        ineli <- matrix(0,s,ncombi)
        ineli[select] <- 1
        ineligible <- cbind(ineligible,ineli)
    }
    cat(ncol(ineligible)," termes ineligibles.\n")
    if( (p!=2) ){
        ineligible <- representative.basep(ineligible,p)
    }
    ## Identification of the last non-zero coefficients in each ineligible trt character
    ineligible.lnz <- apply(ineligible, 2, function(x){max(seq(along=x)[x!=0])})
    ## initialisation of PhiStar by using the first q factors as basic factors
    PhiStar <- diag(r)
    ##
    f <- ncol(PhiStar)
    if(s == f){
        check <- !any(apply(((PhiStar %*% ineligible)%%p)==0, 2, all))
        if(check) return(list(PhiStar))
    }
    ## Calculation of the set of initially admissible elements of U*
    admissible <- t(convertinto.basep(seq((p^r)-1),p))
    nb.admissible <- ncol(admissible)
    ## Backtrack search - preliminaries
    eeU <- list(length=s-f)
    leeU <- rep(NA,s-f)
    neeU <- rep(0,s-f)
    ## Backtrack search
    cat("Recherche d'une solution (algorithme backtrack).\n")
    jprev <- 0  ;  j <- 1
    solved <- FALSE
    while((j > 0)&(!solved)){
        PhiStar <- PhiStar[,seq(f+j-1), drop=FALSE]
        if(jprev < j){
            ineligible.j <- ineligible[ seq(f+j-1), ineligible.lnz==(f+j), drop=FALSE ]
            admissible.keep <- planor.kernelcheck.basep(PhiStar, admissible, ineligible.j, p)
            eeU[[j]] <- seq(nb.admissible)[admissible.keep]
            leeU[j] <- length(eeU[[j]])
            neeU[j] <- 0
        }
        if(neeU[j] < leeU[j]){
            neeU[j] <- neeU[j]+1
            newcolj <- (eeU[[j]])[neeU[j]]
            PhiStar <- cbind(PhiStar,admissible[,newcolj])
            if(j == (s-f)){
                cat("Solution obtenue. ")
                solved <- TRUE
                jprev <- j ; j <- j
            }
            else{
                jprev <- j ; j <- j+1
            }
        }
        else{
            jprev <- j ; j <- j-1
        }
    }
    if(solved){
        ## Construction du plan
        plan <- crossing(rep(p,r),start=0) %*% PhiStar %%p
        ## Sortie
        out <- list(plan=plan, matrice.cle=PhiStar, p=p)
    }
    else{
        cat("Pas de solution. ")
        out <- NULL
    }
    cat("Recherche terminee.\n")
    return(out)
}

#' Fonction générant l'ensemble minimal de représentants des colonnes
#' d'une matrice, en base \eqn{p}
#' @param mat une matrice d'entiers modulo \eqn{p}
#' @param p un nombre entier premier
#' @return une matrice d'entiers modulo \eqn{p}

representative.basep <-
    function(mat,p)
{
    ## generates the minimal set of representatives in base p
    ## of the columns x of matrix mat

    mat <- as.matrix(mat)
    ##
    if(p==2) return(mat %%2)
    ##
    representative <- NULL
    for(j in seq(ncol(mat))){
        x <- mat[,j]
        select <- seq(x)[x != 0]
        nbtocross <- length(select)-1
        if( nbtocross <= 0 ) mat.j <- x
        else{
            select <- select[seq(nbtocross)]
            N <- (p-1)^nbtocross
            mat.j <- matrix(x, nrow(mat), N)
            mat.j[select,] <- t( crossing(rep(p-1,nbtocross),start=1) )
        }
        representative <- cbind(representative, mat.j)
    }
    return(representative %%p)
}

### CONSTRUCTION D'HYPERCUBES LATINS (FONCTIONS FOURNIES PAR B. IOOSS)

#' Tirage aléatoire LHS de N jeux de paramètres
#' (possibilite d'imposer une matrice de corrélations sur les rangs)
#' @references
#'  Stein, M. 1987. Technometrics 29:143-151
#'
#'  Iman and Conover. 1982. Commun. Stat. Simul. Comput. 11(3):311-334
#'
#'  McKay, Conover and Beckman. 1979. Technometrics 21: 239-245
#' @param dim_x nombre de paramètres d'entrée dans le modèle
#' @param nom vecteur des noms des parametres simules
#'              (defini comme : c("V1","V2","V3",...))
#' @param N nombre de jeux de simulations
#' @param lois vecteur contenant les types de distribution de proba
#'               pour chaque entrée:
#'               0=uniforme ; 1=normale ; 2=lognormale ; 3=weibull
#'               4=exponentielle ; 5=beta ; 6=triangulaire ; 7=trapezoidale
#'               10=gumbel.
#'               Par defaut, on prend la loi uniforme
#' @param paramlois tableau avec les parametres de chaque loi (max=4)
#'               pour chaque entree (range par colonne) :
#'               (min,max,0,0) pour uniforme (par défaut : min=0, max=1)
#'               (moy,ecart-type,0,0) pour normale,
#'               (moy du log, ecart-type du log,0,0) pour lognormale,
#'               (forme,echelle,0,0) pour Weibull,
#'               (lambda,0,0,0) pour exponentielle,
#'               (shape1, shape2,0,0) pour beta,
#'               (min,mode,max,0) pour triangulaire,
#'               (min,mode1,mode2,max) pour trapezoidale,
#'               (mode,echelle,0,0) pour Gumbel
#' @param correl 0 \eqn{\rightarrow} pas de correlation entre parametres;
#'               1 \eqn{\rightarrow} pas de correlation, on supprime les correlations
#'               indesirables par la methode des permutations circulaires
#'               2 \eqn{\rightarrow} introduction d'une matrice de correlations sur
#'               les rangs des parametres via le fichier 'matcorrelrank.dat'
#' @param tronq vecteur pour sélectionner ou non une loi tronquée: TRUE pour loi tronquée, FALSE sinon
#' @param paramtronq tableau avec les paramètres de troncature de chaque loi: (min,max) range par colonne
#' @return la matrice des N simulations des \code{dim_x} parametres
#' @author      B. Iooss
#' @note
#'  !!! WARNING !!!! : le LHS et la la troncature ne s'appliquent pas a la
#'                    loi trapezoidale
#'
#' *********************************************************************
#'
#' FONCTION UTILISEE :
#'
#'      truncated.R (fonctions pour lois tronquees)
#'
#' *********************************************************************
#'
#' LIBRAIRIES REQUISES (A INSTALLER)
#'
#'  library(triangle)
#'
#'  library(evd) # Gumbel
#'
# *********************************************************************

samplingLHS <-
    function(dim_x,nom=c(NA),N=1,lois=rep(0,dim_x),
                  paramlois=array(0,dim=c(4,dim_x)),correl=0,
                  tronq=rep(FALSE,dim_x),paramtronq=array(0,dim=c(2,dim_x))){

#---------------------------------------------------------------------------|
#Tirage aleatoire LHS de N jeux de parametres                               |
#(possibilite d'imposer une matrice de correlations sur les rangs)          |
#  References:                                                              |
#  Stein, M. 1987. Technometrics 29:143-151                                 |
#  Iman and Conover. 1982. Commun. Stat. Simul. Comput. 11(3):311-334       |
#  McKay, Conover and Beckman. 1979. Technometrics 21: 239-245              |
#---------------------------------------------------------------------------|
#Arguments obligatoires :                                                   |
#               dim_x : nombre de parametres d'entree dans le modele        |
#                                                                           |
#Arguments facultatifs :                                                    |
#             nom : vecteur des noms des parametres simules                 |
#               (defini comme : c("V1","V2","V3",...))                      |
#             N : nombre de jeux de simulations                             |
#             lois : vecteur contenant les types de distribution de proba   |
#               pour chaque entree                                          |
#               0=uniforme ; 1=normale ; 2=lognormale ; 3=weibull           |
#               4=exponentielle ; 5=beta ; 6=triangulaire ; 7=trapezoidale  |
#               10=gumbel                                                   |
#               Par defaut, on prend la loi uniforme                        |
#             paramlois : tableau avec les parametres de chaque loi (max=4) |
#               pour chaque entree (range par colonne) :                    |
#               (min,max,0,0) pour uniforme (par défaut : min=0, max=1)     |
#               (moy,ecart-type,0,0) pour normale,                          |
#               (moy du log, ecart-type du log,0,0) pour lognormale,        |
#               (forme,echelle,0,0) pour Weibull,                           |
#               (lambda,0,0,0) pour exponentielle,                          |
#               (shape1, shape2,0,0) pour beta,                             |
#               (min,mode,max,0) pour triangulaire,                         |
#               (min,mode1,mode2,max) pour trapezoidale,                    |
#               (mode,echelle,0,0) pour Gumbel                              |
#             correl : 0 -> pas de correlation entre parametres             |
#                      1 -> pas de correlation, on supprime les correlations|
#               indesirables par la methode des permutations circulaires    |
#                      2 -> introduction d'une matrice de correlations sur  |
#               les rangs des parametres via le fichier "matcorrelrank.dat" |
#             tronq : vecteur pour selectionner ou non une loi tronquee     |
#               TRUE pour loi tronquee, FALSE sinon (par défaut)            |
#             paramtronq : tableau avec les parametres de troncature de     |
#               chaque loi : (min,max) range par colonne                    |

#---------------------------------------------------------------------------|
#Sortie : la matrice des N simulations des dim_x parametres                 |
#---------------------------------------------------------------------------|
#      Auteurs : B. Iooss                                                   |
#---------------------------------------------------------------------------|

# !!! WARNING !!!! : le LHS et la la troncature ne s'appliquent pas a la
#                    loi trapezoidale

# *********************************************************************
# FONCTION UTILISEE :
#      truncated.R (fonctions pour lois tronquees)
# *********************************************************************
# LIBRAIRIES REQUISES (A INSTALLER)
#  library(triangle)
#  library(evd) # Gumbel
# *********************************************************************

# si le nom du x n'est pas defini
  for(i in 1:dim_x){
    if(is.na(nom[i])==TRUE) nom[i]<-paste("V",i,sep="")}

# on definit les matrices
  x <- matrix(0,nrow=N,ncol=dim_x,dimnames=list(1:N,nom))
  Mp <- matrix(0,nrow=N,ncol=dim_x,dimnames=list(1:N,nom))
  R <- matrix(0,nrow=N,ncol=dim_x,dimnames=list(1:N,nom))
  R1 <- matrix(0,nrow=N,ncol=dim_x,dimnames=list(1:N,nom))
  M1 <- matrix(0,nrow=N,ncol=dim_x,dimnames=list(1:N,nom))

#on complete les bornes des lois uniformes, triangle et trapeze si necessaire
  for (i in 1:dim_x) {
	if (lois[i]==0 && paramlois[1,i]>=paramlois[2,i]) paramlois[2,i]<-1
	else if (lois[i]==6 && paramlois[1,i]>=paramlois[3,i]) {
		paramlois[2,i]<-0.5
		paramlois[3,i]<-1
	}
	else if (lois[i]==7 && paramlois[1,i]>=paramlois[4,i]) {
		paramlois[2,i]<-0.25
		paramlois[3,i]<-0.75
		paramlois[4,i]<-1
	}}

# Tirages aleatoires uniformes
  ran<-matrix(runif(N*dim_x),nrow=N,ncol=dim_x)

# methode de Stein
  for (i in 1:dim_x){

    idx<-sample(1:N)
    P<-(idx-ran[,i])/N # probabilite de la fonction de repartition

    if (lois[i]==0){ # loi uniforme
      if (tronq[i]==FALSE) x[,i] <- qunif(P,min=paramlois[1,i],max=paramlois[2,i])
      else x[,i] <- q.trunc.distr(P,'unif',trunc.int=paramtronq[,i],min=paramlois[1,i],max=paramlois[2,i])}

    if (lois[i]==1){ # loi normale
      if (tronq[i]==FALSE) x[,i] <-  qnorm(P,mean=paramlois[1,i],sd=paramlois[2,i])
      else x[,i] <- q.trunc.distr(P,'norm',trunc.int=paramtronq[,i],mean=paramlois[1,i],sd=paramlois[2,i])}

    if (lois[i]==2){ # loi lognormale
      if (tronq[i]==FALSE) x[,i] <- qlnorm(P,meanlog=paramlois[1,i],sdlog=paramlois[2,i])
      else x[,i] <- q.trunc.distr(P,'lnorm',trunc.int=paramtronq[,i],meanlog=paramlois[1,i],sdlog=paramlois[2,i])}

    if (lois[i]==3){ # loi weibull
      if (tronq[i]==FALSE) x[,i] <- qweibull(P,shape=paramlois[1,i],scale=paramlois[2,i])
      else x[,i] <- q.trunc.distr(P,'weibull',trunc.int=paramtronq[,i],shape=paramlois[1,i],scale=paramlois[2,i])}

    if (lois[i]==10){ # loi gumbel
      if (tronq[i]==FALSE) x[,i] <- qgumbel(P,loc=paramlois[1,i],scale=paramlois[2,i])
      else x[,i] <- q.trunc.distr(P,'gumbel',trunc.int=paramtronq[,i],loc=paramlois[1,i],scale=paramlois[2,i])}

    if (lois[i]==4){ # loi exponentielle
      if (tronq[i]==FALSE) x[,i]<- qexp(P,rate=paramlois[1,i])
      else x[,i] <- q.trunc.distr(P,'exp',trunc.int=paramtronq[,i],rate=paramlois[1,i])}

    if (lois[i]==5){ # loi beta
      if (tronq[i]==FALSE) x[,i]<- qbeta(P,shape1=paramlois[1,i],shape2=paramlois[2,i])
      else x[,i] <- q.trunc.distr(P,'beta',trunc.int=paramtronq[,i],shape1=paramlois[1,i],shape2=paramlois[2,i])}

    if (lois[i]==6) { # loi triangulaire
      if (tronq[i]==FALSE) x[,i]<- qtriangle(P,a=paramlois[1,i],b=paramlois[3,i],c=paramlois[2,i])
      else x[,i] <- q.trunc.distr(P,'triangle',trunc.int=paramtronq[,i],a=paramlois[1,i],b=paramlois[3,i],c=paramlois[2,i])}

    if (lois[i]==7) { # loi trapezoidale ( !!! pas LHS !!! )
      norm<-1/(paramlois[2,i]-paramlois[1,i])
      normp<-1/(paramlois[4,i]-paramlois[3,i])
      j<-1
      while (j<=N) {
        aux<-runif(1,min=paramlois[1,i],max=paramlois[4,i])
        auxp<-runif(1)
        if (aux<=paramlois[2,i]) {
          if (auxp<=norm*(aux-paramlois[1,i])) {
            x[j,i]<-aux
            j<-j+1}}
        else if (aux>=paramlois[3,i]) {
          if (auxp<=normp*(paramlois[4,i]-aux)) {
            x[j,i]<-aux
            j<-j+1}}
        else {
          x[j,i]<-aux
          j<-j+1}}}
  }

 # Correlations : methode des permutations circulaires
  if (correl > 0){

# matrice de correlation de l'echantillon LHS
    for (i in 1:dim_x) R[,i] <- rank(x[,i])
    Ttt <- cor(x,method="spearman")
    Q <- chol(Ttt,pivot=Ttt) # decomposition de Cholesky

    if (correl==2){
# lecture de la matrice de correlation des rangs
      FOUT <- file("matcorrelrank.dat","r")
      C <- read.table(file=FOUT)
      close(FOUT)
      P <- chol(as.matrix(C),pivot=TRUE) # decomposition de Cholesky

#  Qinv <- solve(Q) # matrice inverse
#  St <- t(P %*% Qinv)
      St <- solve(t(Q),t(P)) # + efficace que les 2 lignes precedentes
      M1 <- R %*% St
    }
    else M1 <- t(solve(Q,t(R)))

    for (i in 1:dim_x) R1[,i] <- rank(M1[,i])

    for (j in 1:dim_x){
      for (i in 1:N){
        k <- 1
        while (R[k,j] != R1[i,j]) k <- k+1
        Mp[i,j] <- x[k,j]
      }}
  }
  else Mp <- x

  return(Mp)

}

#' Tirage aleatoire d'un plan LHS optimal de N jeux de parametres
#'  Le plan peut etre maximin, distance-optimal ou S-optimal
#' @param dim_x nombre de parametres d'entree dans le modele
#' @param nom vecteur des noms des parametres simules
#'               (defini comme : \code{c("V1","V2","V3",...)})
#' @param N nombre de jeux de simulations
#' @param lois vecteur contenant les types de distribution de proba
#'               pour chaque entree
#'               0=uniforme ; 1=normale ; 2=lognormale ; 3=weibull
#'               4=exponentielle ; 5=beta ; 6=triangulaire ; 7=trapezoidale
#'               10=gumbel
#'               Par defaut, on prend la loi uniforme
#' @param paramlois tableau avec les parametres de chaque loi (max=4)
#'               pour chaque entree (range par colonne) :
#'               (min,max,0,0) pour uniforme (par défaut : min=0, max=1)
#'               (moy,ecart-type,0,0) pour normale,
#'               (moy du log, ecart-type du log,0,0) pour lognormale,
#'               (forme,echelle,0,0) pour Weibull,
#'               (lambda,0,0,0) pour exponentielle,
#'               (shape1, shape2,0,0) pour beta,
#'               (min,mode,max,0) pour triangulaire,
#'               (min,mode1,mode2,max) pour trapezoidale,
#'               (mode,echelle,0,0) pour Gumbel
#' @param tronq TODO
#' @param paramtronq TODO
#' @param optimal type d'optimalite pour le LHS (par defaut 'maximin')
#'               'distance' pour distance-optimal, 'S' pour S-optimal
#' @param dup facteur pour le nb de points candidats dans les fcts
#'               maximinLHS (plan maximin) et improvedLHS (plan dist-optimal)
#' @param pop option de la fct geneticLHS (plan S-optimal)
#'               Taper help(geneticLHS) pour en savoir plus
#' @param gen option de la fct geneticLHS (plan S-optimal)
#'               Taper help(geneticLHS) pour en savoir plus
#' @param pMut option de la fct geneticLHS (plan S-optimal)
#'               Taper help(geneticLHS) pour en savoir plus
#' @return la matrice des N simulations des \code{dim_x} parametres
#' @author     B. Iooss
#' @note
#' !!! WARNING !!!! : le LHS et la la troncature ne s'appliquent pas a la
#'                    loi trapezoidale
#'
#' *********************************************************************
#'
#' FONCTION UTILISEE :
#'
#'      truncated.R (fonctions pour lois tronquees)
#'
#' *********************************************************************
#'
#' LIBRAIRIES REQUISES (A INSTALLER)
#'
#'  library(triangle)
#'
#'  library(lhs)
#'
#'  library(evd) # Gumbel
#'
#' *********************************************************************
### mis à jour le 12/05/2010

samplingOptLHS<-function(dim_x,nom=c(NA),N=1,lois=rep(0,dim_x),
                   paramlois=array(0,dim=c(4,dim_x)),
                   tronq=rep(FALSE,dim_x),paramtronq=array(0,dim=c(2,dim_x)),
                   optimal="maximin",dup=1,pop=100,gen=4,pMut=0.1){

#---------------------------------------------------------------------------|
#Tirage aleatoire d'un plan LHS optimal de N jeux de parametres             |
#  Le plan peut etre maximin, distance-optimal ou S-optimal                 |
#---------------------------------------------------------------------------|
#Arguments obligatoires :                                                   |
#               dim_x : nombre de parametres d'entree dans le modele        |
#                                                                           |
#Arguments facultatifs :                                                    |
#             nom : vecteur des noms des parametres simules                 |
#               (defini comme : c("V1","V2","V3",...))                      |
#             N : nombre de jeux de simulations                             |
#             lois : vecteur contenant les types de distribution de proba   |
#               pour chaque entree                                          |
#               0=uniforme ; 1=normale ; 2=lognormale ; 3=weibull           |
#               4=exponentielle ; 5=beta ; 6=triangulaire ; 7=trapezoidale  |
#               10=gumbel                                                   |
#               Par defaut, on prend la loi uniforme                        |
#             paramlois : tableau avec les parametres de chaque loi (max=4) |
#               pour chaque entree (range par colonne) :                    |
#               (min,max,0,0) pour uniforme (par défaut : min=0, max=1)     |
#               (moy,ecart-type,0,0) pour normale,                          |
#               (moy du log, ecart-type du log,0,0) pour lognormale,        |
#               (forme,echelle,0,0) pour Weibull,                           |
#               (lambda,0,0,0) pour exponentielle,                          |
#               (shape1, shape2,0,0) pour beta,                             |
#               (min,mode,max,0) pour triangulaire,                         |
#               (min,mode1,mode2,max) pour trapezoidale,                    |
#               (mode,echelle,0,0) pour Gumbel                              |
#             tronq : vecteur pour selectionner ou non une loi tronquee     |
#               TRUE pour loi tronquee, FALSE sinon (par défaut)            |
#             paramtronq : tableau avec les parametres de troncature de     |
#               chaque loi : (min,max) range par colonne                    |
#             optimal : type d'optimalite pour le LHS (par defaut "maximin")|
#               "distance" pour distance-optimal, "S" pour S-optimal        |
#             dup : facteur pour le nb de points candidats dans les fcts    |
#               maximinLHS (plan maximin) et improvedLHS (plan dist-optimal)|
#             pop, gen, pMut : options de la fct geneticLHS (plan S-optimal)|
#               Taper help(geneticLHS) pour en savoir plus                  |
#---------------------------------------------------------------------------|
#Sortie : la matrice des N simulations des dim_x parametres                 |
#---------------------------------------------------------------------------|
#      Auteur : B. Iooss                                                    |
#---------------------------------------------------------------------------|

# !!! WARNING !!!! : le LHS et la la troncature ne s'appliquent pas a la
#                    loi trapezoidale

# *********************************************************************
# FONCTION UTILISEE :
#      truncated.R (fonctions pour lois tronquees)
# *********************************************************************
# LIBRAIRIES REQUISES (A INSTALLER)
#  library(triangle)
#  library(lhs)
#  library(evd) # Gumbel
# *********************************************************************

# si le nom du x n'est pas defini
  for(i in 1:dim_x){
    if(is.na(nom[i])==TRUE) nom[i]<-paste("V",i,sep="")}

#on complete les bornes des lois uniformes, triangle et trapeze si necessaire
  for (i in 1:dim_x) {
	if (lois[i]==0 && paramlois[1,i]>=paramlois[2,i]) paramlois[2,i]<-1
	else if (lois[i]==6 && paramlois[1,i]>=paramlois[3,i]) {
		paramlois[2,i]<-0.5
		paramlois[3,i]<-1
	}
	else if (lois[i]==7 && paramlois[1,i]>=paramlois[4,i]) {
		paramlois[2,i]<-0.25
		paramlois[3,i]<-0.75
		paramlois[4,i]<-1
	}}

# Plan LHS maximin
  if (optimal=="distance") x <- improvedLHS(n=N,k=dim_x,dup=dup)
# Plan LHS S-optimal
  if (optimal=="S") x <- geneticLHS(n=N,k=dim_x,pop=pop,gen=gen,pMut=pMut)
# Plan LHS maximin
  else x <- maximinLHS(n=N,k=dim_x,dup=dup)

  x <- matrix(x,nrow=N,ncol=dim_x,dimnames=list(1:N,nom))

# Tirages aleatoires uniformes
  ran<-matrix(runif(N*dim_x),nrow=N,ncol=dim_x)

  for (i in 1:dim_x){

    idx<-sample(1:N)
    P<-(idx-ran[,i])/N # probabilite de la fonction de repartition

    if (lois[i]==0){ # loi uniforme
      if (tronq[i]==FALSE) x[,i] <- qunif(P,min=paramlois[1,i],max=paramlois[2,i])
      else x[,i] <- q.trunc.distr(P,'unif',trunc.int=paramtronq[,i],min=paramlois[1,i],max=paramlois[2,i])}

    if (lois[i]==1){ # loi normale
      if (tronq[i]==FALSE) x[,i] <-  qnorm(P,mean=paramlois[1,i],sd=paramlois[2,i])
      else x[,i] <- q.trunc.distr(P,'norm',trunc.int=paramtronq[,i],mean=paramlois[1,i],sd=paramlois[2,i])}

    if (lois[i]==2){ # loi lognormale
      if (tronq[i]==FALSE) x[,i] <- qlnorm(P,meanlog=paramlois[1,i],sdlog=paramlois[2,i])
      else x[,i] <- q.trunc.distr(P,'lnorm',trunc.int=paramtronq[,i],meanlog=paramlois[1,i],sdlog=paramlois[2,i])}

    if (lois[i]==3){ # loi weibull
      if (tronq[i]==FALSE) x[,i] <- qweibull(P,shape=paramlois[1,i],scale=paramlois[2,i])
      else x[,i] <- q.trunc.distr(P,'weibull',trunc.int=paramtronq[,i],shape=paramlois[1,i],scale=paramlois[2,i])}

    if (lois[i]==10){ # loi gumbel
      if (tronq[i]==FALSE) x[,i] <- qgumbel(P,loc=paramlois[1,i],scale=paramlois[2,i])
      else x[,i] <- q.trunc.distr(P,'gumbel',trunc.int=paramtronq[,i],loc=paramlois[1,i],scale=paramlois[2,i])}

    if (lois[i]==4){ # loi exponentielle
      if (tronq[i]==FALSE) x[,i]<- qexp(P,rate=paramlois[1,i])
      else x[,i] <- q.trunc.distr(P,'exp',trunc.int=paramtronq[,i],rate=paramlois[1,i])}

    if (lois[i]==5){ # loi beta
      if (tronq[i]==FALSE) x[,i]<- qbeta(P,shape1=paramlois[1,i],shape2=paramlois[2,i])
      else x[,i] <- q.trunc.distr(P,'beta',trunc.int=paramtronq[,i],shape1=paramlois[1,i],shape2=paramlois[2,i])}

    if (lois[i]==6) { # loi triangulaire
      if (tronq[i]==FALSE) x[,i]<- qtriangle(P,a=paramlois[1,i],b=paramlois[3,i],c=paramlois[2,i])
      else x[,i] <- q.trunc.distr(P,'triangle',trunc.int=paramtronq[,i],a=paramlois[1,i],b=paramlois[3,i],c=paramlois[2,i])}

    if (lois[i]==7) { # loi trapezoidale ( !!! pas LHS !!! )
      norm<-1/(paramlois[2,i]-paramlois[1,i])
      normp<-1/(paramlois[4,i]-paramlois[3,i])
      j<-1
      while (j<=N) {
        aux<-runif(1,min=paramlois[1,i],max=paramlois[4,i])
        auxp<-runif(1)
        if (aux<=paramlois[2,i]) {
          if (auxp<=norm*(aux-paramlois[1,i])) {
            x[j,i]<-aux
            j<-j+1}}
        else if (aux>=paramlois[3,i]) {
          if (auxp<=normp*(paramlois[4,i]-aux)) {
            x[j,i]<-aux
            j<-j+1}}
        else {
          x[j,i]<-aux
          j<-j+1}}}
  }

  return(x)

}

#' Tirage aleatoire simple de N jeux de parametres
#' @param dim_x nombre de parametres d'entree dans le modele
#' @param             nom : vecteur des noms des parametres simules
#'               (defini comme : c("V1","V2","V3",...))
#' @param N nombre de jeux de simulations
#' @param lois vecteur contenant les types de distribution de proba
#'               pour chaque entree
#'               0=uniforme ; 1=normale ; 2=lognormale ; 3=weibull
#'               4=exponentielle ; 5=beta ; 6=triangulaire ; 7=trapezoidale
#'               10=gumbel
#'               Par defaut, on prend la loi uniforme
#' @param paramlois tableau avec les parametres de chaque loi (max=4)
#'               pour chaque entree (range par colonne) :
#'               (min,max,0,0) pour uniforme (par défaut : min=0, max=1)
#'               (moy,ecart-type,0,0) pour normale,
#'               (moy du log, ecart-type du log,0,0) pour lognormale,
#'               (forme,echelle,0,0) pour Weibull,
#'               (lambda,0,0,0) pour exponentielle,
#'               (shape1, shape2,0,0) pour beta,
#'               (min,mode,max,0) pour triangulaire,
#'               (min,mode1,mode2,max) pour trapezoidale,
#'               (mode,echelle,0,0) pour Gumbel
#' @param tronq vecteur pour selectionner ou non une loi tronquee
#'               TRUE pour loi tronquee, FALSE sinon (par défaut)
#' @param paramtronq tableau avec les parametres de troncature de
#'               chaque loi : (min,max) range par colonne
#' @return la matrice des N simulations des \code{dim_x} parametres
#' @author B. Iooss
#' @note
#' !!! WARNING !!!! : la troncature ne s'applique pas a la loi trapezoidale
#'
#' *********************************************************************
#'
#' FONCTION UTILISEE :
#'
#'      truncated.R (fonctions pour lois tronquees)
#'
#' *********************************************************************
#'
#' LIBRAIRIES REQUISES (A INSTALLER)
#'
#'  library(triangle)
#'
#'  library(evd) # Gumbel
#'
#' *********************************************************************

samplingSimple <-
function(dim_x,nom=c(NA),N=1,lois=rep(0,dim_x),
                  paramlois=array(0,dim=c(4,dim_x)),
                  tronq=rep(FALSE,dim_x),paramtronq=array(0,dim=c(2,dim_x))){
#---------------------------------------------------------------------------|
#Tirage aleatoire simple de N jeux de parametres                            |
#---------------------------------------------------------------------------|
#Arguments obligatoires :                                                   |
#               dim_x : nombre de parametres d'entree dans le modele        |
#                                                                           |
#Arguments facultatifs :                                                    |
#             nom : vecteur des noms des parametres simules                 |
#               (defini comme : c("V1","V2","V3",...))                      |
#             N : nombre de jeux de simulations                             |
#             lois : vecteur contenant les types de distribution de proba   |
#               pour chaque entree                                          |
#               0=uniforme ; 1=normale ; 2=lognormale ; 3=weibull           |
#               4=exponentielle ; 5=beta ; 6=triangulaire ; 7=trapezoidale  |
#               10=gumbel                                                   |
#               Par defaut, on prend la loi uniforme                        |
#             paramlois : tableau avec les parametres de chaque loi (max=4) |
#               pour chaque entree (range par colonne) :                    |
#               (min,max,0,0) pour uniforme (par défaut : min=0, max=1)     |
#               (moy,ecart-type,0,0) pour normale,                          |
#               (moy du log, ecart-type du log,0,0) pour lognormale,        |
#               (forme,echelle,0,0) pour Weibull,                           |
#               (lambda,0,0,0) pour exponentielle,                          |
#               (shape1, shape2,0,0) pour beta,                             |
#               (min,mode,max,0) pour triangulaire,                         |
#               (min,mode1,mode2,max) pour trapezoidale,                    |
#               (mode,echelle,0,0) pour Gumbel                              |
#             tronq : vecteur pour selectionner ou non une loi tronquee     |
#               TRUE pour loi tronquee, FALSE sinon (par défaut)            |
#             paramtronq : tableau avec les parametres de troncature de     |
#               chaque loi : (min,max) range par colonne                    |
#---------------------------------------------------------------------------|
#Sortie : la matrice des N simulations des dim_x parametres                 |
#---------------------------------------------------------------------------|
#      Auteur : B. Iooss                                                    |
#---------------------------------------------------------------------------|


# !!! WARNING !!!! : la troncature ne s'applique pas a la loi trapezoidale

# *********************************************************************
# FONCTION UTILISEE :
#      truncated.R (fonctions pour lois tronquees)
# *********************************************************************
# LIBRAIRIES REQUISES (A INSTALLER)
#  library(triangle)
#  library(evd) # Gumbel
# *********************************************************************

  for(i in 1:dim_x){ # si le nom du x n'est pas defini
    if(is.na(nom[i])==TRUE) nom[i]<-paste("V",i,sep="")}

  x<-matrix(0,nrow=N,ncol=dim_x,dimnames=list(1:N,nom))

#on complete les bornes des lois uniformes
  for (i in 1:dim_x) {
	if (lois[i]==0 && paramlois[1,i]>=paramlois[2,i]) paramlois[2,i]<-1
	else if (lois[i]==6 && paramlois[1,i]>=paramlois[3,i]) {
		paramlois[2,i]<-0.5
		paramlois[3,i]<-1
	}
	else if (lois[i]==7 && paramlois[1,i]>=paramlois[4,i]) {
		paramlois[2,i]=0.25
		paramlois[3,i]=0.75
		paramlois[4,i]=1
	}}

  for(i in 1:dim_x){

    if (lois[i]==0){ # loi uniforme
      if (tronq[i]==FALSE) x[,i] <- runif(N,min=paramlois[1,i],max=paramlois[2,i])
      else x[,i] <- r.trunc.distr(N,'unif',trunc.int=paramtronq[,i],min=paramlois[1,i],max=paramlois[2,i])}

    if (lois[i]==1){ # loi normale
      if (tronq[i]==FALSE) x[,i] <-  rnorm(N,mean=paramlois[1,i],sd=paramlois[2,i])
      else x[,i] <- r.trunc.distr(N,'norm',trunc.int=paramtronq[,i],mean=paramlois[1,i],sd=paramlois[2,i])}

    if (lois[i]==2){ # loi lognormale
      if (tronq[i]==FALSE) x[,i] <- rlnorm(N,meanlog=paramlois[1,i],sdlog=paramlois[2,i])
      else x[,i] <- r.trunc.distr(N,'lnorm',trunc.int=paramtronq[,i],meanlog=paramlois[1,i],sdlog=paramlois[2,i])}

    if (lois[i]==3){ # loi weibull
      if (tronq[i]==FALSE) x[,i] <- rweibull(N,shape=paramlois[1,i],scale=paramlois[2,i])
      else x[,i] <- r.trunc.distr(N,'weibull',trunc.int=paramtronq[,i],shape=paramlois[1,i],scale=paramlois[2,i])}

    if (lois[i]==10){ # loi gumbel
      if (tronq[i]==FALSE) x[,i] <- rgumbel(N,loc=paramlois[1,i],scale=paramlois[2,i])
      else x[,i] <- r.trunc.distr(N,'gumbel',trunc.int=paramtronq[,i],loc=paramlois[1,i],scale=paramlois[2,i])}

    if (lois[i]==4){ # loi exponentielle
      if (tronq[i]==FALSE) x[,i]<- rexp(N,rate=paramlois[1,i])
      else x[,i] <- r.trunc.distr(N,'exp',trunc.int=paramtronq[,i],rate=paramlois[1,i])}

    if (lois[i]==5){ # loi beta
      if (tronq[i]==FALSE) x[,i]<- rbeta(N,shape1=paramlois[1,i],shape2=paramlois[2,i])
      else x[,i] <- r.trunc.distr(N,'beta',trunc.int=paramtronq[,i],shape1=paramlois[1,i],shape2=paramlois[2,i])}

    if (lois[i]==6) { # loi triangulaire
      if (tronq[i]==FALSE) x[,i]<- rtriangle(N,a=paramlois[1,i],b=paramlois[3,i],c=paramlois[2,i])
      else x[,i] <- r.trunc.distr(N,'triangle',trunc.int=paramtronq[,i],a=paramlois[1,i],b=paramlois[3,i],c=paramlois[2,i])}

    if (lois[i]==7) { # loi trapezoidale
      norm<-1/(paramlois[2,i]-paramlois[1,i])
      normp<-1/(paramlois[4,i]-paramlois[3,i])
      j<-1
      while (j<=N) {
        aux<-runif(1,min=paramlois[1,i],max=paramlois[4,i])
        auxp<-runif(1)
        if (aux<=paramlois[2,i]) {
          if (auxp<=norm*(aux-paramlois[1,i])) {
            x[j,i]<-aux
            j<-j+1}}
        else if (aux>=paramlois[3,i]) {
          if (auxp<=normp*(paramlois[4,i]-aux)) {
            x[j,i]<-aux
            j<-j+1}}
        else {
          x[j,i]<-aux
          j<-j+1}}}
  }

  return(x)
}


########## AJOUTE LE 12/05/2010 ##########

########### de R. FAIVRE ###########

#' Transformation d'un échantillon d'une loi uniforme vers une loi normale
#' de même moyenne et d'écart.type assurant que la loi uniforme couvre une
#' probabilité égale à l'argument couverture de la loi normale
#' @param x échantillon d'une loi considérée uniforme
#' @param param ligne d'un data.frame de type xxx.factors
#' @param couverture une probabilité entre 0 et 1
#' @examples
#'   convertU2N(seq(8,10,length=11),fungus.factors["Topt",])

convertU2N <-
    function(x,param, couverture= 0.95)
{
    ##
    ## Transformation d'un echantillon d'une loi uniforme vers une loi normale
    ##    (mu,ecart.type) sur un intervalle
    ##    couvrant un taux de couverture egal a couverture
    ##
    ## USAGE : convertU2N(seq(8,10,length=11),fungus.factors["Topt",])
    vinf = qnorm( (1 - couverture)/2)
    mu = (param$binf+param$bsup)/2
    ecart.type = (param$bsup - param$binf)/ 2 / abs(vinf)
    xt = (x - param$binf)/(param$bsup-param$binf)
    qnorm( (1 - couverture)/2 + xt* couverture,mean=mu,sd = ecart.type)
}

########### de B. IOOSS ###########

# *********************************************************************
# truncated.R : fonctions pour lois tronquees
#
#   Les lois tronquees normale, lognormale et gumbel sont definies avec
#   des noms specifiques :
#          dtnorm   ptnorm   qtnorm   rtnorm
#          dtlnorm  ptlnorm  qtlnorm  rtlnorm
#          dtgumbel ptgumbel qtgumbel rtgumbel
#
#   Toute autre loi de R peut etre tronquee avec les fonctions  :
#          d.trunc.distr p.trunc.distr q.trunc.distr r.trunc.distr
#
#**********************************************************************
#      Auteurs : G. Pujol et B. Iooss
#**********************************************************************

#library(evd) # Loi Gumbel

# *********************************************************************

# The Truncated Normal Distribution

#' Fonctions associées à la loi Normale tronquée
#' @title The Truncated Normal distribution
#' @name loiNormaleTronquee
#' @aliases dtnorm ptnorm qtnorm rtnorm
#' @usage
#'  dtnorm(x, mean = 0, sd = 1, min = -1e6, max = 1e6)
#'
#'  ptnorm(q, mean = 0, sd = 1, min = -1e6, max = 1e6)
#'
#'  qtnorm(p, mean = 0, sd = 1, min = -1e6, max = 1e6)
#'
#'  rtnorm(n, mean = 0, sd = 1, min = -1e6, max = 1e6)
#' @param x vecteur de quantiles
#' @param q vecteur de quantiles
#' @param p vecteur de probabilités
#' @param n taille de l'échantillon aléatoire à générer
#' @param mean moyenne de la loi à tronquer
#' @param sd écart-type de la loi à tronquer
#' @param min borne inférieure de la troncature
#' @param max borne supérieure de la troncature
#' @return densités, probabilités, quantiles, échantillon aléatoire

dtnorm <- function(x, mean = 0, sd = 1, min = -1e6, max = 1e6){
# density
  out = dnorm(x, mean, sd) / (pnorm(max, mean, sd) - pnorm(min, mean, sd))
  out[(x < min) | (x > max)] = 0
  return(out)
}

ptnorm <- function(q, mean = 0, sd = 1, min = -1e6, max = 1e6){
# distribution function
  out = (pnorm(q, mean, sd) - pnorm(min, mean, sd)) /
    (pnorm(max, mean, sd) - pnorm(min, mean, sd))
  out[q < min] = 0
  out[q > max] = 1
  return(out)
}

qtnorm <- function(p, mean = 0, sd = 1, min = -1e6, max = 1e6){
# quantile function
  return(qnorm((1 - p) * pnorm(min, mean, sd) + p * pnorm(max, mean, sd),
               mean, sd))
}

rtnorm <- function(n, mean = 0, sd = 1, min = -1e6, max = 1e6){
# random generation
  return(qtnorm(runif(n), mean, sd, min, max))
}

#**************************************
# The Truncated Lognormal Distribution

#' Fonctions associées à la loi LogNormale tronquée
#' @title The Truncated LogNormal distribution
#' @name loiLogNormaleTronquee
#' @aliases dtlnorm ptlnorm qtlnorm rtlnorm
#' @usage
#'  dtlnorm(x, meanlog = 0, sdlog = 1, min = -1e6, max = 1e6)
#'
#'  ptlnorm(q, meanlog = 0, sdlog = 1, min = -1e6, max = 1e6)
#'
#'  qtlnorm(p, meanlog = 0, sdlog = 1, min = -1e6, max = 1e6)
#'
#'  rtlnorm(n, meanlog = 0, sdlog = 1, min = -1e6, max = 1e6)
#' @param x vecteur de quantiles
#' @param q vecteur de quantiles
#' @param p vecteur de probabilités
#' @param n taille de l'échantillon aléatoire à générer
#' @param meanlog moyenne de la loi à tronquer
#' @param sdlog écart-type de la loi à tronquer
#' @param min borne inférieure de la troncature
#' @param max borne supérieure de la troncature
#' @return densités, probabilités, quantiles, échantillon aléatoire

# density
dtlnorm <- function(x, meanlog = 0, sdlog = 1, min = -1e6, max = 1e6){
  out = dlnorm(x, meanlog, sdlog) / (plnorm(max, meanlog, sdlog) - plnorm(min, meanlog, sdlog))
  out[(x < min) | (x > max)] = 0
  return(out)
}

# distribution function
ptlnorm <- function(q, meanlog = 0, sdlog = 1, min = -1e6, max = 1e6){
  out = (plnorm(q, meanlog, sdlog) - plnorm(min, meanlog, sdlog)) /
    (plnorm(max, meanlog, sdlog) - plnorm(min, meanlog, sdlog))
  out[q < min] = 0
  out[q > max] = 1
  return(out)
}

# quantile function
qtlnorm <- function(p, meanlog = 0, sdlog = 1, min = -1e6, max = 1e6){
  return(qlnorm((1 - p) * plnorm(min, meanlog, sdlog) + p * plnorm(max, meanlog, sdlog),
               meanlog, sdlog))
}

# random generation
rtlnorm <- function(n, meanlog = 0, sdlog = 1, min = -1e6, max = 1e6){
  return(qtlnorm(runif(n), meanlog, sdlog, min, max))
}

# ***************************************
# The Truncated Gumbel Distribution

#' Fonctions associées à la loi de Gumbel tronquée
#' @title The Truncated Gumbel distribution
#' @name loiGumbelTronquee
#' @aliases dtgumbel ptgumbel qtgumbel rtgumbel
#' @usage
#'  dtgumbel(x, loc = 0, scale = 1, min = -1e6, max = 1e6)
#'
#'  ptgumbel(q, loc = 0, scale = 1, min = -1e6, max = 1e6)
#'
#'  qtgumbel(p, loc = 0, scale = 1, min = -1e6, max = 1e6)
#'
#'  rtgumbel(n, loc = 0, scale = 1, min = -1e6, max = 1e6)
#' @param x vecteur de quantiles
#' @param q vecteur de quantiles
#' @param p vecteur de probabilités
#' @param n taille de l'échantillon aléatoire à générer
#' @param loc paramètre de position de la loi à tronquer
#' @param scale paramètre d'échelle de la loi à tronquer
#' @param min borne inférieure de la troncature
#' @param max borne supérieure de la troncature
#' @return densités, probabilités, quantiles, échantillon aléatoire

# density
dtgumbel <- function(x, loc = 0, scale = 1, min = -1e6, max = 1e6){
  out = dgumbel(x, loc, scale) / (pgumbel(max, loc, scale) - pgumbel(min, loc, scale))
  out[(x < min) | (x > max)] = 0
  return(out)
}

# distribution function
ptgumbel <- function(q, loc = 0, scale = 1, min = -1e6, max = 1e6){
  out = (pgumbel(q, loc, scale) - pgumbel(min, loc, scale)) /
    (pgumbel(max, loc, scale) - pgumbel(min, loc, scale))
  out[q < min] = 0
  out[q > max] = 1
  return(out)
}

# quantile function
qtgumbel <- function(p, loc = 0, scale = 1, min = -1e6, max = 1e6){
  return(qgumbel((1 - p) * pgumbel(min, loc, scale) + p * pgumbel(max, loc, scale),
               loc, scale))
}

# random generation
rtgumbel <- function(n, loc = 0, scale = 1, min = -1e6, max = 1e6){
  return(qtgumbel(runif(n), loc, scale, min, max))
}

# ******************************************************
# lois tronquees generiques
# ******************************************************

## Deux modes :
## d.trunc.distr(x, distr = 'norm', trunc.int = c(-2, 2), mean = 0, sd = 1)
## d.trunc.distr(x, distr = c('dnorm', 'pnorm', 'qnorm', 'rnorm'), trunc.int = c(-2, 2), mean = 0, sd = 1)

#' Fonctions génériques pour loi tronquée
#' @title Fonctions génériques pour loi tronquée
#' @name loiGeneriqueTronquee
#' @aliases d.trunc.distr p.trunc.distr q.trunc.distr r.trunc.distr LoiGeneriqueTronquee
#' @usage
#'  d.trunc.distr(x, distr, trunc.int, ...)
#'
#'  p.trunc.distr(q, distr, trunc.int, ...)
#'
#'  q.trunc.distr(p, distr, trunc.int, ...)
#'
#'  r.trunc.distr(n, distr, trunc.int, ...)
#' @param x vecteur de quantiles
#' @param q vecteur de quantiles
#' @param p vecteur de probabilités
#' @param n taille de l'échantillon aléatoire à générer
#' @param distr intitulé de la loi à tronquer
#' @param trunc.int bornes de la troncature
#' @param ... paramètres de la loi à tronquer
#' @return densités, probabilités, quantiles, échantillon aléatoire
#' @examples
#'  d.trunc.distr(x=c(-2.5,-1.96,-1,0,1,1.96,2.5), distr = 'norm', trunc.int = c(-2, 2), mean = 0, sd = 1)
#'  d.trunc.distr(x=c(-2.5,-1.96,-1,0,1,1.96,2.5), distr = c('dnorm', 'pnorm', 'qnorm', 'rnorm'), trunc.int = c(-2, 2), mean = 0, sd = 1)

d.trunc.distr <- function(x, distr, trunc.int, ...) {
  if (length(distr) == 1) {
    distr <- paste(c('d', 'p', 'q', 'r'), distr, sep = '')
  }
  p.min <- do.call(distr[2], list(trunc.int[1], ...))
  p.max <- do.call(distr[2], list(trunc.int[2], ...))
  d.x <- do.call(distr[1], list(x, ...))
  out <- d.x / (p.max - p.min)
  out[(x < trunc.int[1]) | (x > trunc.int[2])] <- 0
  return(out)
}

p.trunc.distr <- function(q, distr, trunc.int, ...) {
  if (length(distr) == 1) {
    distr <- paste(c('d', 'p', 'q', 'r'), distr, sep = '')
  }
  p.min <- do.call(distr[2], list(trunc.int[1], ...))
  p.max <- do.call(distr[2], list(trunc.int[2], ...))
  p.q <- do.call(distr[2], list(q, ...))
  out <- (p.q - p.min) / (p.max - p.min)
  out[q < trunc.int[1]] <- 0
  out[q > trunc.int[2]] <- 1
  return(out)
}

q.trunc.distr <- function(p, distr, trunc.int, ...) {
  if (length(distr) == 1) {
    distr <- paste(c('d', 'p', 'q', 'r'), distr, sep = '')
  }
  p.min <- do.call(distr[2], list(trunc.int[1], ...))
  p.max <- do.call(distr[2], list(trunc.int[2], ...))
  do.call(distr[3], list((1 - p) * p.min + p * p.max, ...))
}

r.trunc.distr <- function(n, distr, trunc.int, ...) {
  q.trunc.distr(runif(n), distr, trunc.int, ...)
}


##### SUITE A REMARQUE DE CLAUDE BRUCHOU, UNE VERSION MEXICO DE MORRIS  #####

#' Adaptation de la méthode de Morris pour l'EC Mexico
#' @title Méthode de Morris à la sauce mexicaine
#' @param model voir la méthode morris de la librairie sensitivity
#' @param factors voir la méthode morris de la librairie sensitivity
#' @param r voir la méthode morris de la librairie sensitivity
#' @param design voir la méthode morris de la librairie sensitivity
#' @param binf voir la méthode morris de la librairie sensitivity
#' @param bsup voir la méthode morris de la librairie sensitivity
#' @param scale voir la méthode morris de la librairie sensitivity
#' @param ... voir la méthode morris de la librairie sensitivity
#' @return voir la méthode morris de la librairie sensitivity
#' @note Corrige un problème détecté dans la version 1.0 de sensitivity en
#' normalisant les facteurs d'entrée avant les calculs principaux

morris.mexico <-
    function(model = NULL, factors, r, design, binf = 0, bsup = 1,
             scale = TRUE, ...)
{
    morrisOutput <- morris(model = NULL, factors=factors, r=r, design=design,
                           binf = 0, bsup = 1,
                           scale = TRUE, ...)
    morrisOutput$X <- t(binf + t(morrisOutput$X)*(bsup-binf))

    if(!is.null(model)){
        response <- model(morrisOutput$X, ...)
        design <- tell(morrisOutput, response)
    }

    morrisOutput$note <- "adaptation ECmexico2012"
    return(morrisOutput)
}

##### SUITE A VISIO DU 31/5, INTEGRATION DE FONCTIONS DU TP1  #####

### FONCTION DE TIRAGE UNIFORME (C. BRUCHOU)

#' Tirage uniforme dans un pavé de R^K
#' @param PAV vecteur des coordonnées entières d'un pavé codées de 1 à Nbclass
#' @param binf vecteur des bornes inf des gammes des facteurs
#' @param bsup vecteur des bornes sup des gammes des facteurs
#' @param Nbclass niveau de discrétisation des gammes des facteurs
#' @return vecteur à \eqn{K} éléments
#' @examples
#'  TP1tirage( PAV=c(1,2,3),binf=c(-10,0,100),bsup=c(10,5,600),Nbclass=5)

TP1tirage <-
    function(PAV,  binf, bsup, Nbclass)
{
    ##----------------------------------------------------------------------
    ##  TIRAGE uniforme des coordonnees d'un point
    ##          dans le pave PAV de R^K , K = Nb facteurs
    ##----------------------------------------------------------------------
    ##  binf et bsup : vecteurs des bornes inf et sup des facteurs,
    ##  Nbclass : nbre de classes par facteur,
    ##  PAV : vecteur de K numeros de classe (1 -> Nbclass)
    ##  sortie = vecteur de K éléments
    K = length(PAV)
    bornes = matrix(NA,nrow=K, ncol=2)
    bornes[,1]= binf + (PAV-1)*(bsup-binf)/Nbclass
    bornes[,2]= binf + PAV*(bsup-binf)/Nbclass
    cc = numeric(K)
    for(i in 1:K) cc[i] = runif(1,min=bornes[i,1],max=bornes[i,2])
    cc
}

#' Construction du plan avec tirage dans les pavés défini par un plan P
#' @param P matrice à Nbfac colonnes  codée par des entiers de 1 à Nbclass
#' @param nrep nbre de tirages par pavé [entier]
#' @param Nbclass niveau de discrétisation des gammes des facteurs
#' @param binf vecteur des bornes inf des gammes des facteurs
#' @param bsup = vecteur des bornes sup des gammes des facteurs
#' @return liste contenant les matrices des coordonnées entières (Plan.rep) et
#'         réelles (xx) des points tirés au hasard
#' @examples
#'  TP1pavage( rbind(1:3,c(2,2,2)),binf=c(-10,0,100),bsup=c(10,5,600),Nbclass=5)

TP1pavage <- function(P, nrep =3, Nbclass = 2, binf,  bsup) {
    Nbfac = ncol(P)
    M=NULL ; for(j in 1:nrep) M = rbind(M, P)
    Plan.rep= M
    list(P.out = Plan.rep,
         xx = t(apply( Plan.rep, 1, TP1tirage, binf, bsup, Nbclass) ))
}

#' Calcul et représentation graphique des indices principaux et totaux
#' issus d'une anova
#' @param table.aov table d'anova issue de la fonction aov()
#' @param noms vecteur des labels des facteurs
#' @param modeleAOV modèle d'anova créé avec formula()
#' @param titre titre du graphique

TP1indices.aov <- function(table.aov, noms , modeleAOV , titre=''){
    vv <- terms( modeleAOV , keep.order = FALSE)
    ## SS = somme des carrés,
    Nbfac <- length(noms)
    SS <- table.aov[[1]][2]
    neffets <- nrow(SS)-1
    SS.fac <- SS[1:neffets,]
    SStot <- sum(SS.fac)
    vv1 <- attr(vv, "factors")
    Itot <- rep(NA,Nbfac)
    Iprinc <- SS.fac[1:Nbfac]/SStot

    for(i in 1:Nbfac) Itot[i] <- sum(SS.fac[vv1[i+1,]==1])/SStot
    M <- rbind(Iprinc, Itot-Iprinc)

    barplot( M, col=c("lightblue", "blue"), names.arg=noms)
    title(titre)

    return(M)
}

#' Diagramme des fréquences des valeurs des facteurs échantillonnées
#' issus d'une anova
#' @param etude.morris structure issue de la fonction morris du package sensitivity

TP1histo <- function(etude.morris){
    noms =  etude.morris$factors
    Nbfac = length(noms)
    Nbtraj=  etude.morris$r
    numtraj = rep(1:Nbtraj, each = Nbfac+1)
    par(mfrow=c(3,3))
    for(i in 1:Nbfac) barplot(table(unlist(
         tapply(etude.morris$X[,i], numtraj,unique))),
         col= 'blue',main= noms[i])
}

#' Graphiques de corrélation entre facteurs X et sortie y de la FC
#' issus d'une anova
#' @param etude.morris structure issue de la fonction morris du package sensitivity
#' @param transfo si TRUE, recodage de la matrice X codée dans [0,1]
#' @param binf vecteur des bornes inférieures des gammes des facteurs
#' @param bsup vecteur des bornes supérieures des gammes des facteurs

TP1corr <- function(etude.morris, transfo=TRUE, binf , bsup ){
    noms =  etude.morris$factors
    Nbfac = length(noms)
    if(transfo)  X = t(binf + t(etude.morris$X)*(bsup-binf))
    if(!transfo) X =  etude.morris$X
    par(mfrow=c(3,3),ask=TRUE)
    for(i in 1:Nbfac){
	plot(X[,i], etude.morris$y, pch=21, bg='lightblue')
        title(noms[i])
	lines( lowess(X[,i] , etude.morris$y), lwd=2, col="red")
    }
}

#' Calcul d'intervalles de confiance pour la méthode Morris
#' @param etude.morris structure issue de la fonction morris du package sensitivity

TP1.ICmorris <- function( etude.morris){
    noms =  etude.morris$factors
    Nbfac = length(noms)
    Nbtraj = etude.morris$r
    sigma = sqrt(apply(etude.morris$ee,2,var))
    IC.mu = apply(abs(etude.morris$ee),2, t.test)
    tabICmu = matrix(0,Nbfac,2); tabICsig = matrix(0,Nbfac,2)
    rownames(tabICmu) = noms ; rownames(tabICsig) = noms
    for(i in 1:Nbfac) {
        tabICmu[i,]  = IC.mu[[i]]$conf.int
        tabICsig[i,] = c(sigma[i]*(Nbtraj-1)^.5/qchisq(.975,Nbtraj-1)^.5,
                sigma[i]*(Nbtraj-1)^.5/qchisq(.025,Nbtraj-1)^.5)
    }
    print('IC mu*') ; print(tabICmu); print('IC sigma');print(tabICsig);
    mu.star <- apply(etude.morris$ee, 2, function(x) mean(abs(x)))
    xlim1= c(min(tabICmu[,1]), max(tabICmu[,2]))
    ylim1= c(min(tabICsig[,1]), max(tabICsig[,2]))
    plot(mu.star,sigma,xlim=xlim1,ylim=ylim1)
    for(i in 1:Nbfac) { segments(tabICmu[i,1], sigma[i], tabICmu[i,2],      sigma[i],col= 'red')
                        segments(mu.star[i],tabICsig[i,1], mu.star[i], tabICsig[i,2], sigma[i],col= 'red')
                    }
}

