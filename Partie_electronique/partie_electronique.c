#include <stdlib.h>
#include <stdio.h>
      int etape;
      char tab[5];
void afficheChiffre(char chiffre,char position){
 LATA = 0x01 << position;     //   les PINs de AN0 a AN3 sont des canaux analogiques
      switch(chiffre){
       case 0:LATD0_bit = 1;LATD1_bit = 1;LATD2_bit = 1;LATD3_bit = 1;
              LATD4_bit = 1;LATD5_bit = 1;LATD6_bit = 0;LATD7_bit = 0;
       break;
       case 1:LATD0_bit = 0;LATD1_bit = 1;LATD2_bit = 1;LATD3_bit = 0;
              LATD4_bit = 0;LATD5_bit = 0;LATD6_bit = 0;LATD7_bit = 0;
       break;

       case 2:LATD0_bit = 1;LATD1_bit = 1;LATD2_bit = 0;LATD3_bit = 1;
              LATD4_bit = 1;LATD5_bit = 0;LATD6_bit = 1;LATD7_bit = 0;
       break;
       case 3:LATD0_bit = 1;LATD1_bit = 1;LATD2_bit = 1;LATD3_bit = 1;
              LATD4_bit = 0;LATD5_bit = 0;LATD6_bit = 1;LATD7_bit = 0;
       break;
       case 4:LATD0_bit = 0;LATD1_bit = 1;LATD2_bit = 1;LATD3_bit = 0;
              LATD4_bit = 0;LATD5_bit = 1;LATD6_bit = 1;LATD7_bit = 0;
       break;
       case 5:LATD0_bit = 1;LATD1_bit = 0;LATD2_bit = 1;LATD3_bit = 1;
              LATD4_bit = 0;LATD5_bit = 1;LATD6_bit = 1;LATD7_bit = 0;
       break;
       case 6:LATD0_bit = 1;LATD1_bit = 0;LATD2_bit = 1;LATD3_bit = 1;
              LATD4_bit = 1;LATD5_bit = 1;LATD6_bit = 1;LATD7_bit = 0;
       break;
       case 7:LATD0_bit = 1;LATD1_bit = 1;LATD2_bit = 1;LATD3_bit = 0;
              LATD4_bit = 0;LATD5_bit = 0;LATD6_bit = 0;LATD7_bit = 0;
       break;
       case 8:LATD0_bit = 1;LATD1_bit = 1;LATD2_bit = 1;LATD3_bit = 1;
              LATD4_bit = 1;LATD5_bit = 1;LATD6_bit = 1;LATD7_bit = 0;
       break;
       case 9:LATD0_bit = 1;LATD1_bit = 1;LATD2_bit = 1;LATD3_bit = 1;
              LATD4_bit = 0;LATD5_bit = 1;LATD6_bit = 1;LATD7_bit = 0;
       break;
       case 10:LATD0_bit = 0;LATD1_bit = 0;LATD2_bit = 0;LATD3_bit = 0;
              LATD4_bit = 0;LATD5_bit = 0;LATD6_bit = 0;LATD7_bit = 1;
       break;
       default:
       break;
      }
     }


BINAS (int nombre , char  tab [] )
   {
   int i;
   int div, rest;
   for(i=0; i<4; i++)
  {
    tab[i]= '0';
   }
 tab[4]=0;
  for(i=0; i<4; i++)
  {
   div=nombre/10;
   rest=nombre-(10*div);
   tab[3-i]=rest+0x30;
   nombre=div;
 }
}
 void afficheNombre(float var){
      char chiffre0,chiffre1,chiffre2,chiffre3;
      int counter;
      chiffre3 = var/1000;
      var -= chiffre3*1000;
      chiffre2 = var/100;
      var -= chiffre2*100;
      chiffre1 = var/10;
      var -= chiffre1*10;
      chiffre0 = (char)var;
      counter =1;
      while(counter <= 128){
        afficheChiffre(chiffre0,0);
        Delay_ms(1);
        afficheChiffre(chiffre1,1);
        Delay_ms(1);
        afficheChiffre(chiffre2,2);
        Delay_ms(1);
        afficheChiffre(chiffre3,3);
        Delay_ms(1);
        counter++;
      }
     }
 void main(void) {
      int compte = 0;
      float nombreaffiche = 0;
      UART1_Init(9600); // Initialisation du module UART à 9600 bps
      ANSELB = 0x00;   //   toutes les entrées sont numeriques
      TRISA = 0x00;
      TRISB = 0x01;   //   Entrée Microcontrôleur
      TRISD = 0x00;
LATA = 0x00;    // attribution des valeurs
      LATB = 0x00;
      LATD = 0x00;
      etape = 0;     // initialisation de l'étape impulsion et calcul
//afficheNombre((int)nombreaffiche);
while(1){
        switch(etape) {
case 0:   // génération d'une impulsion vers US
         LATB1_bit = 1;
         compte = 0;
         etape++ ;
         break;
         case 1:   // attente 1ms et retour à zéro
              Delay_ms(1);
              LATB1_bit = 0;
              etape++ ;
              break;
         case 2:  // vérifier si RB0 =1 (existence d'un ECHO sur le PORTB puis attendre 10ms
                 if(PORTB & 0x01){
                    while(PORTB & 0x01){
                                compte++;
                                delay_us(10);
                                }
                                etape++;
                                 }
                         break;
         case 3:  // calcul de la distance en cm (58)
          nombreaffiche = (float)compte*10/58.0;
         Delay_ms(1);
         etape=0;
         break;
        }
        if (etape == 3) {
        BINAS(nombreaffiche, tab );
        UART1_Write_Text(tab );  // envoi des infos reçus  via l UART
        UART1_Write(13); //change de ligne //Code ASCII equivalent pour ENTRE
        UART1_Write(10);//retour à la ligne //Code ASCII equivalent pour (CTRL+H)
        delay_ms(1000);

        }
      }
}
