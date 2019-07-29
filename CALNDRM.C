#include <osbind.h>
#include <gemdefs.h>
#include <obdefs.h>
#include "CALNDR.H"
/*******************************************************/
/*   CALNDR.C   MEGAMAX Laser C                        */
/*                                                     */
/*   Simple program to demo Atari 16 bit RSC files     */
/*                                                     */
/*******************************************************/

#define FINGER 3

int days[] = {
 D01, D02, D03, D04, D05, D06, D07, D08, D09, D10,
 D11, D12, D13, D14, D15, D16, D17, D18, D19, D20,
 D21, D22, D23, D24, D25, D26, D27, D28, D29, D30,
 D31, D32, D33, D34, D35,D36, D37,  D38, D39, D40,
 D41, D42};

int dow[] = { 0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4 };
int dom[]  = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

char * sd[] = {
 "  ", "  ","  ","  ","  ", 
 "  ", "  ","  ","  ","  ", 
 "  ", "  ","  ","  ","  ", 
 "  ", "  ","  ","  ","  ", 
 "  ", "  ","  ","  ","  ", 
 "  ", "  ","  ","  ","  ", 
 "  ", "  ","  ","  ","  ", 
 "  ", "  ","  ","  ","  ", 
 "  ", "  ","  "
};

char tl[25], dt[25];
int  cyy, cmm, cdd;
char * mTxt[] =
{
 "January", "February", "March", "April", "May", "June", 
 "July", "August", "September", "October","November", "December"
};

/* v_opnvwk input array */
int work_in[11],
 work_out[57],
 pxyarray[10],
 contrl[12],
 intin[128],
 ptsin[128],
 intout[128],
 ptsout[128];

/* Global variables */
int handle, dum, drive_map;
int x, y, w, h, n_x, n_y; 
OBJECT * dialog; 

/* set the text object value */
int setText(i, value, thk) 
int i;
char * value;
int thk;
{
   TEDINFO *ob_tedinfo;
   ob_tedinfo = (TEDINFO *) dialog[i].ob_spec;	
   ob_tedinfo -> te_ptext = value;
   ob_tedinfo -> te_thickness = thk;
   return 1;
}

/* Find our DOW  */
int dayofweek(d, m, y)
int d, m, y; 
{ 
    if(y%400 == 0) dom[2] = 29;
    y -= m < 3; 
    return ( y + y/4 - y/100 + y/400 + dow[m-1] + d) % 7; 
} 

/* draw the calendar */
int paintDate(yy, mm, dd) 
int yy, mm, dd; 
{
  int lg, dinm, d, ss;  
  dinm = dom[mm-1];  
  lg = dayofweek(1,mm,yy);
  for (d = 1; d <= 42; d++)
  {
     if(d > lg && d <= dinm + lg) 
        sprintf(sd[d-1]," %d ", (d-lg) );
     else 
        sprintf(sd[d-1], "    "); 
	 
     if(yy == cyy && mm == cmm && (d - lg) == cdd)
       setText(days[d-1], sd[d-1] ,254);
      else 
       setText(days[d-1], sd[d-1] ,0); 
      
  sprintf(tl," %s %d ", mTxt[mm-1], yy);
  setText(MONTHS, tl, 254);
  }
  return 1;
}

/* Display the date picked by the user */
int pickDate(yy, mm, dd,ch)
int yy, mm, dd, ch;
{
  int lg, dinm, d;  
  dinm = dom[mm-1];  
  lg = dayofweek(1,mm,yy);
  for (d = 1; d <= 42; d++)
  {
     if(d > lg && d <= (dinm + lg)  && ch == days[d-1]) 
     {
       sprintf(dt,"[3][ You Picked:  %s %d %d ][ok] ",mTxt[mm-1], (d - lg), yy );
       form_alert (1, dt);
       return 0;
      }    
  }
  return 1;
}
		
/*main program */
int main()
{	
    short handle, junk;		
    int ch, rez, i;
    int date, yy, mm, dd;

    date = Tgetdate ();     /* get the system date */
    cdd = date & 0x001f;    /* split it by mm dd yyyy */
    cmm = (date >> 5) & 0x000f;
    cyy = ((date >> 9) & 0x007f) + 80;
    cyy = cyy % 100;
    if(cyy < 60)           /* still doing Y2K 20 years later */
       cyy += 2000;
    else 
       cyy += 1900;

    appl_init();                          	        /* start AES */
	handle=graf_handle(&junk, &junk, &junk, &junk);	/* find AES handle */
	for (i=0; i<10; work_in[i++] = 1);
        work_in[10] = 2;
	v_opnvwk(work_in, &handle, work_out);			/* open workstation */
        rez = Getrez();	
	if(rez == 0)
	{	
	  form_alert (1,"[1][Med/High Rez only][I am done]");
	}	
	else if (!rsrc_load ("CALNDR.RSC")) 
	{ 
      form_alert (1,"[1][CALNDR.RSC is missing][All done]");
	}
	else
	{
     yy = cyy;
     mm = cmm;
     dd = cmm;
     rsrc_gaddr(R_TREE, CLNDR, &dialog) ; 
     form_center (dialog, &x, &y,  &w , &h);
	 graf_mouse(FINGER,&junk);
     form_dial (FMD_START, 0, 0, 10, 10, x, y, w, h) ;
	 form_dial (FMD_GROW,  0, 0, 10, 10, x, y, w, h) ;
	 paintDate(yy, mm, dd);
     objc_draw (dialog, 0, 10, x, y, w, h); 
     do {							/* loop intil exit click */
         ch = form_do (dialog, CLNDR);       
         if(ch == LF) mm--;           /* move to nxt/prv month */     
         if(ch == RT) mm++;	         
         if(ch != OK)                 
          {
            if(mm > 12 )  {  mm =  1; yy++; }    
            if(mm < 1)    {  mm = 12; yy--; }    
	        paintDate(yy, mm, dd);
                objc_draw (dialog, 0, 10, x, y, w, h);
          }
   	     } while (ch != OK && pickDate(yy,mm,dd,ch));
	     form_dial (FMD_SHRINK , 0, 0, 10, 10, x, y, w, h) ;
             form_dial (FMD_FINISH , 0, 0, 10, 10, x, y, w, h) ;
        }	
       v_clsvwk(handle);		/* close workstation */
       appl_exit();				/* shutdown AES */
       return 0;
}
