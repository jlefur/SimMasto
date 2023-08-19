###############################################################################
### Partie I. question 1
###############################################################################
### Chargement des Rdata
# entrer ici le chemin vers le dossier Rdata
rm(list=ls())
setwd("C:/partage_erwin/2012-EcoleChercheurAnaSensi/CleMEXICO2012/Ateliers/Atelier_ISIS_EC2012/DossierAtelierAS_Stagiaire/Rdata/Y2")

#setwd("F:/Formation/Atelier SA/RData_new")
#install.packages("sensitivity")
library(sensitivity)

load("random_ss_regles_152params_Y2.Rdata")
load("random_MPA_152params_Y2.Rdata")
load("random_TAC_152params_Y2.Rdata")

# on les renomme pour chaque strategie de gestion
ssr = random_ss_regles_152params_Y2
amp = random_MPA_152params_Y2
tac = random_TAC_152params_Y2

# on les stoque dans une liste pour pouvoir faire des boucles
listMgtStr = list()
listMgtStr[[1]] = ssr
listMgtStr[[2]] = amp
listMgtStr[[3]] = tac
namesMgtStr = c("ss_regles","AMP","TAC")

# les valeurs des X à chaque simulation et les valeurs de Y correspondantes
# sont stoquées dans isis.simule (le df du plan d'exerience)

# Les Y sont les colonnes 153 à 158 du plan d'experience
# creation d'un dataframe avec le num de la colonne et le nom de la sortie correspondante
OutputsCol = data.frame(cbind(colonne = c(153:158),output = c("Y6","Y3","Y4","Y1","Y5","Y2")),stringsAsFactors=F)
OutputsCol$colonne = as.numeric(OutputsCol$colonne)

#-------------------------------------------------------------------------------
# Exploration de Y

### Histogrammes de Y / stratégie
for(i in 1:6){ # les 6 variables de sortie
  y = 152+i
  jpeg(filename = paste("Graphiques/PIQ1_",OutputsCol$output[i],"_histo.jpg",sep=""), width = 900)
    par(mfrow = c(1,3))
    hist(ssr$isis.simule[,y],main=paste(names(ssr$isis.simule)[y],"Sans règle"),col="lightBlue")
    hist(amp$isis.simule[,y],main=paste(names(ssr$isis.simule)[y],"AMP"),col="lightPink")
    hist(tac$isis.simule[,y],main=paste(names(ssr$isis.simule)[y],"TAC"),col="lightGreen")
  dev.off()
}

### Relation Y ~ xi
library(lattice)
for(ms in 1:3){   
	ms <- 1
	# pour chaque mesure de gestion
  planexp = listMgtStr[[ms]]$isis.simule
  for(i in 1:6){                      # pour chaque Y
    i<-1
	y = 152+i
j<-0
    for(j in c(0,42,84)){             # pour les differents X  (tout ne tient pas dans un graph, on traite les X, 42 x 42)
      jpeg(filename=paste("Graphiques/PIQ2_",OutputsCol$output[i],"_",namesMgtStr[ms],"_V",j+1,"-",j+42,".jpg",sep=""), width = 900, height = 900)
        plot(xyplot(planexp[,y] ~ planexp[,j+1]+planexp[,j+2]+planexp[,j+3]+planexp[,j+4]+planexp[,j+5]+planexp[,j+6]+planexp[,j+7]+planexp[,j+8]
        +planexp[,j+9]+planexp[,j+10]+planexp[,j+11]+planexp[,j+12]+planexp[,j+13]+planexp[,j+14]+planexp[,j+15]+planexp[,j+16]+planexp[,j+17]+planexp[,j+18]
        +planexp[,j+19]+planexp[,j+20]+planexp[,j+21]+planexp[,j+22]+planexp[,j+23]+planexp[,j+24]+planexp[,j+25]+planexp[,j+26]+planexp[,j+27]+planexp[,j+28]
        +planexp[,j+29]+planexp[,j+30]+planexp[,j+31]+planexp[,j+32]+planexp[,j+33]+planexp[,j+34]+planexp[,j+35]+planexp[,j+36]+planexp[,j+37]+planexp[,j+38]
        +planexp[,j+39]+planexp[,j+40]+planexp[,j+41]+planexp[,j+42] ,xlab="",ylab=""
        ,outer = TRUE,panel = panel.smoothScatter, aspect = "fill",main =paste(namesMgtStr[ms]," ",OutputsCol$output[i]," Variables",j+1,"à",j+42)))
      dev.off()
      #Sys.sleep(30)
    }

    j = 126
    jpeg(filename=paste("Graphiques/PIQ1_",OutputsCol$output[i],"_",namesMgtStr[ms],"_V",j+1,"-",j+26,".jpg",sep=""), width = 900, height = 900)
      plot(xyplot(planexp[,y] ~ planexp[,j+1]+planexp[,j+2]+planexp[,j+3]+planexp[,j+4]+planexp[,j+5]+planexp[,j+6]+planexp[,j+7]+planexp[,j+8]
      +planexp[,j+9]+planexp[,j+10]+planexp[,j+11]+planexp[,j+12]+planexp[,j+13]+planexp[,j+14]+planexp[,j+15]+planexp[,j+16]+planexp[,j+17]+planexp[,j+18]
      +planexp[,j+19]+planexp[,j+20]+planexp[,j+21]+planexp[,j+22]+planexp[,j+23]+planexp[,j+24]+planexp[,j+25]+planexp[,j+26],xlab="",ylab=""
      ,outer = TRUE,panel = panel.smoothScatter, aspect = "fill",main =paste(namesMgtStr[ms]," ",OutputsCol$output[i]," Variables",j+1,"à",j+26)))
    dev.off()
  }
}
