###############################################################################
### Partie II. question 7
###############################################################################
### Chargement des Rdata
# entrer ici le chemin vers le dossier Rdata
setwd("F:/Formation/Atelier SA/RData_new")
load("sobol_7param_ss_regles.Rdata")
load("sobol_7param_MPA.Rdata")
load("sobol_7param_TAC.Rdata")

load("fast_7param_ss_regles.Rdata")
load("fast_7param_MPA.Rdata")
load("fast_7param_TAC.Rdata")

nameMgtStr = c("ss_regles","MPA","TAC")

library(sensitivity)
#-------------------------------------------------------------------------------
# Fast
# fast99(model = NULL, factors = 7, n = 150, M = 6, q = "qunif",
#    q.arg = list(min = 0, max = 1))

# que fait la fonction ?
?fast

for(fms in 1:3){ # pour chaque mesure de gestion
  for(output in 1:6){  # pour chaque sortie
    eval(parse(text=paste("res = fast_7param_",nameMgtStr[fms],"_Y",output,"$analysis_result",sep="")))
    print(res)
    jpeg(filename = paste("F:/Formation/Atelier SA/Graphiques/PIIQ7_FAST_",nameMgtStr[fms],"_Y",output,".jpg",sep=""))
    par(las=2,mar=c(15,3,2,2))
    plot(res, las=2)
    title(main = paste("Fast ",nameMgtStr[fms]," Y",output,sep=""))
    dev.off()
  }
}


# Sobol

# que fait la fonction ?
?sobol

for(fms in 1:3){   # pour chaque mesure de gestion
  for(output in 1:6){   # pour chaque sortie
    eval(parse(text=paste("res = sobol_7param_",nameMgtStr[fms],"_Y",output,"$analysis_result",sep="")))
    print(res)
    jpeg(filename = paste("F:/Formation/Atelier SA/Graphiques/PIIQ7_Sobol_",nameMgtStr[fms],"_Y",output,".jpg",sep=""))
    par(las=2,mar=c(15,3,2,2))
    plot(res, las=2)
    title(main = paste("Sobol ",nameMgtStr[fms]," Y",output,sep=""))
    dev.off()
  }
}


