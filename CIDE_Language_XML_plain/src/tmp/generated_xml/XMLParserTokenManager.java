/* Generated By:JavaCC: Do not edit this line. XMLParserTokenManager.java */
package tmp.generated_xml;
import java.io.*;
import java.util.*;
import cide.gast.*;
import cide.gparser.*;
import cide.gparser.*;
public class XMLParserTokenManager implements XMLParserConstants
{
  public  java.io.PrintStream debugStream = System.out;
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_5(int pos, long active0)
{
   switch (pos)
   {
      default :
         return -1;
   }
}
private final int jjStartNfa_5(int pos, long active0)
{
   return jjMoveNfa_5(jjStopStringLiteralDfa_5(pos, active0), pos + 1);
}
private final int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private final int jjStartNfaWithStates_5(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_5(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_5()
{
   switch(curChar)
   {
      case 60:
         jjmatchedKind = 14;
         return jjMoveStringLiteralDfa1_5(0x7a000L);
      default :
         return jjMoveNfa_5(0, 0);
   }
}
private final int jjMoveStringLiteralDfa1_5(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_5(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 33:
         return jjMoveStringLiteralDfa2_5(active0, 0x60000L);
      case 47:
         if ((active0 & 0x8000L) != 0L)
            return jjStopAtPos(1, 15);
         break;
      case 63:
         if ((active0 & 0x10000L) != 0L)
         {
            jjmatchedKind = 16;
            jjmatchedPos = 1;
         }
         return jjMoveStringLiteralDfa2_5(active0, 0x2000L);
      default :
         break;
   }
   return jjStartNfa_5(0, active0);
}
private final int jjMoveStringLiteralDfa2_5(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_5(0, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_5(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 45:
         return jjMoveStringLiteralDfa3_5(active0, 0x20000L);
      case 91:
         return jjMoveStringLiteralDfa3_5(active0, 0x40000L);
      case 120:
         return jjMoveStringLiteralDfa3_5(active0, 0x2000L);
      default :
         break;
   }
   return jjStartNfa_5(1, active0);
}
private final int jjMoveStringLiteralDfa3_5(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_5(1, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_5(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 45:
         if ((active0 & 0x20000L) != 0L)
            return jjStopAtPos(3, 17);
         break;
      case 67:
         return jjMoveStringLiteralDfa4_5(active0, 0x40000L);
      case 109:
         return jjMoveStringLiteralDfa4_5(active0, 0x2000L);
      default :
         break;
   }
   return jjStartNfa_5(2, active0);
}
private final int jjMoveStringLiteralDfa4_5(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_5(2, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_5(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 68:
         return jjMoveStringLiteralDfa5_5(active0, 0x40000L);
      case 108:
         if ((active0 & 0x2000L) != 0L)
            return jjStopAtPos(4, 13);
         break;
      default :
         break;
   }
   return jjStartNfa_5(3, active0);
}
private final int jjMoveStringLiteralDfa5_5(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_5(3, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_5(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 65:
         return jjMoveStringLiteralDfa6_5(active0, 0x40000L);
      default :
         break;
   }
   return jjStartNfa_5(4, active0);
}
private final int jjMoveStringLiteralDfa6_5(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_5(4, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_5(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 84:
         return jjMoveStringLiteralDfa7_5(active0, 0x40000L);
      default :
         break;
   }
   return jjStartNfa_5(5, active0);
}
private final int jjMoveStringLiteralDfa7_5(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_5(5, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_5(6, active0);
      return 7;
   }
   switch(curChar)
   {
      case 65:
         return jjMoveStringLiteralDfa8_5(active0, 0x40000L);
      default :
         break;
   }
   return jjStartNfa_5(6, active0);
}
private final int jjMoveStringLiteralDfa8_5(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_5(6, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_5(7, active0);
      return 8;
   }
   switch(curChar)
   {
      case 91:
         if ((active0 & 0x40000L) != 0L)
            return jjStopAtPos(8, 18);
         break;
      default :
         break;
   }
   return jjStartNfa_5(7, active0);
}
private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private final int jjMoveNfa_5(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 1;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0xefffffffffffffffL & l) == 0L)
                     break;
                  kind = 19;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  kind = 19;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjstateSet[jjnewStateCnt++] = 0;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 1 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjStopStringLiteralDfa_3(int pos, long active0)
{
   switch (pos)
   {
      default :
         return -1;
   }
}
private final int jjStartNfa_3(int pos, long active0)
{
   return jjMoveNfa_3(jjStopStringLiteralDfa_3(pos, active0), pos + 1);
}
private final int jjStartNfaWithStates_3(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_3(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_3()
{
   switch(curChar)
   {
      case 47:
         return jjMoveStringLiteralDfa1_3(0x2000000L);
      case 61:
         return jjStopAtPos(0, 26);
      case 63:
         return jjMoveStringLiteralDfa1_3(0x1000000L);
      default :
         return jjMoveNfa_3(1, 0);
   }
}
private final int jjMoveStringLiteralDfa1_3(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_3(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 62:
         if ((active0 & 0x1000000L) != 0L)
            return jjStopAtPos(1, 24);
         else if ((active0 & 0x2000000L) != 0L)
            return jjStopAtPos(1, 25);
         break;
      default :
         break;
   }
   return jjStartNfa_3(0, active0);
}
private final int jjMoveNfa_3(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 13;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 1:
                  if ((0x100002600L & l) != 0L)
                  {
                     if (kind > 21)
                        kind = 21;
                     jjCheckNAdd(0);
                  }
                  else if (curChar == 34)
                     jjCheckNAddTwoStates(11, 12);
                  else if (curChar == 39)
                     jjCheckNAddTwoStates(8, 9);
                  else if (curChar == 62)
                  {
                     if (kind > 23)
                        kind = 23;
                  }
                  break;
               case 0:
                  if ((0x100002600L & l) == 0L)
                     break;
                  if (kind > 21)
                     kind = 21;
                  jjCheckNAdd(0);
                  break;
               case 2:
                  if ((0x3ff600000000000L & l) == 0L)
                     break;
                  if (kind > 22)
                     kind = 22;
                  jjAddStates(0, 1);
                  break;
               case 3:
                  if (curChar == 58)
                     jjstateSet[jjnewStateCnt++] = 4;
                  break;
               case 5:
                  if ((0x3ff600000000000L & l) == 0L)
                     break;
                  if (kind > 22)
                     kind = 22;
                  jjstateSet[jjnewStateCnt++] = 5;
                  break;
               case 6:
                  if (curChar == 62 && kind > 23)
                     kind = 23;
                  break;
               case 7:
                  if (curChar == 39)
                     jjCheckNAddTwoStates(8, 9);
                  break;
               case 8:
                  if ((0xffffff7fffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(8, 9);
                  break;
               case 9:
                  if (curChar == 39 && kind > 27)
                     kind = 27;
                  break;
               case 10:
                  if (curChar == 34)
                     jjCheckNAddTwoStates(11, 12);
                  break;
               case 11:
                  if ((0xfffffffbffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(11, 12);
                  break;
               case 12:
                  if (curChar == 34 && kind > 27)
                     kind = 27;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 1:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 22)
                     kind = 22;
                  jjCheckNAddTwoStates(2, 3);
                  break;
               case 2:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 22)
                     kind = 22;
                  jjCheckNAddTwoStates(2, 3);
                  break;
               case 4:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 22)
                     kind = 22;
                  jjCheckNAdd(5);
                  break;
               case 5:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 22)
                     kind = 22;
                  jjCheckNAdd(5);
                  break;
               case 8:
                  jjAddStates(2, 3);
                  break;
               case 11:
                  jjAddStates(4, 5);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 8:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(2, 3);
                  break;
               case 11:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(4, 5);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 13 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjMoveStringLiteralDfa0_1()
{
   switch(curChar)
   {
      case 63:
         return jjMoveStringLiteralDfa1_1(0x80000000L);
      default :
         return 1;
   }
}
private final int jjMoveStringLiteralDfa1_1(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      return 1;
   }
   switch(curChar)
   {
      case 62:
         if ((active0 & 0x80000000L) != 0L)
            return jjStopAtPos(1, 31);
         break;
      default :
         return 2;
   }
   return 2;
}
private final int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 93:
         return jjMoveStringLiteralDfa1_0(0x200000000L);
      default :
         return 1;
   }
}
private final int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      return 1;
   }
   switch(curChar)
   {
      case 93:
         return jjMoveStringLiteralDfa2_0(active0, 0x200000000L);
      default :
         return 2;
   }
}
private final int jjMoveStringLiteralDfa2_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return 2;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      return 2;
   }
   switch(curChar)
   {
      case 62:
         if ((active0 & 0x200000000L) != 0L)
            return jjStopAtPos(2, 33);
         break;
      default :
         return 3;
   }
   return 3;
}
private final int jjMoveStringLiteralDfa0_4()
{
   return jjMoveNfa_4(0, 0);
}
private final int jjMoveNfa_4(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 5;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 1:
                  if ((0x3ff600000000000L & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjAddStates(6, 7);
                  break;
               case 2:
                  if (curChar == 58)
                     jjstateSet[jjnewStateCnt++] = 3;
                  break;
               case 4:
                  if ((0x3ff600000000000L & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjstateSet[jjnewStateCnt++] = 4;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjCheckNAddTwoStates(1, 2);
                  break;
               case 1:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjCheckNAddTwoStates(1, 2);
                  break;
               case 3:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjCheckNAdd(4);
                  break;
               case 4:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 20)
                     kind = 20;
                  jjCheckNAdd(4);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 5 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
private final int jjMoveStringLiteralDfa0_2()
{
   switch(curChar)
   {
      case 45:
         return jjMoveStringLiteralDfa1_2(0x20000000L);
      default :
         return 1;
   }
}
private final int jjMoveStringLiteralDfa1_2(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      return 1;
   }
   switch(curChar)
   {
      case 45:
         return jjMoveStringLiteralDfa2_2(active0, 0x20000000L);
      default :
         return 2;
   }
}
private final int jjMoveStringLiteralDfa2_2(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return 2;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      return 2;
   }
   switch(curChar)
   {
      case 62:
         if ((active0 & 0x20000000L) != 0L)
            return jjStopAtPos(2, 29);
         break;
      default :
         return 3;
   }
   return 3;
}
static final int[] jjnextStates = {
   2, 3, 8, 9, 11, 12, 1, 2, 
};
public static final String[] jjstrLiteralImages = {
null, null, null, null, null, null, null, null, null, null, null, null, null, 
"\74\77\170\155\154", "\74", "\74\57", "\74\77", "\74\41\55\55", 
"\74\41\133\103\104\101\124\101\133", null, null, null, null, null, "\77\76", "\57\76", "\75", null, null, null, 
null, null, null, null, };
public static final String[] lexStateNames = {
   "LexCDATA", 
   "LexPI", 
   "LexComment", 
   "LexElement_Inside", 
   "LexElement_Start", 
   "DEFAULT", 
};
public static final int[] jjnewLexState = {
   -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 3, 4, 4, 1, 2, 0, -1, 3, -1, -1, 5, 5, 
   5, -1, -1, -1, 5, -1, 5, -1, 5, 
};
static final long[] jjtoToken = {
   0x2afdfe001L, 
};
static final long[] jjtoSkip = {
   0x200000L, 
};
static final long[] jjtoSpecial = {
   0x200000L, 
};
static final long[] jjtoMore = {
   0x150000000L, 
};
protected CharStream input_stream;
private final int[] jjrounds = new int[13];
private final int[] jjstateSet = new int[26];
protected char curChar;
public XMLParserTokenManager(CharStream stream){
   input_stream = stream;
}
public XMLParserTokenManager(CharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}
public void ReInit(CharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 13; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
public void ReInit(CharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
public void SwitchTo(int lexState)
{
   if (lexState >= 6 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   String im = jjstrLiteralImages[jjmatchedKind];
   t.image = (im == null) ? input_stream.GetImage() : im;
   t.beginLine = input_stream.getBeginLine();
   t.beginColumn = input_stream.getBeginColumn();
   t.endLine = input_stream.getEndLine();
   t.endColumn = input_stream.getEndColumn();
   t.length = input_stream.getLength();
   t.offset = input_stream.getOffset();
   return t;
}

int curLexState = 5;
int defaultLexState = 5;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

public Token getNextToken() 
{
  int kind;
  Token specialToken = null;
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {   
   try   
   {     
      curChar = input_stream.BeginToken();
   }     
   catch(java.io.IOException e)
   {        
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      matchedToken.specialToken = specialToken;
      return matchedToken;
   }

   for (;;)
   {
     switch(curLexState)
     {
       case 0:
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_0();
         if (jjmatchedPos == 0 && jjmatchedKind > 32)
         {
            jjmatchedKind = 32;
         }
         break;
       case 1:
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_1();
         if (jjmatchedPos == 0 && jjmatchedKind > 30)
         {
            jjmatchedKind = 30;
         }
         break;
       case 2:
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_2();
         if (jjmatchedPos == 0 && jjmatchedKind > 28)
         {
            jjmatchedKind = 28;
         }
         break;
       case 3:
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_3();
         break;
       case 4:
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_4();
         break;
       case 5:
         jjmatchedKind = 0x7fffffff;
         jjmatchedPos = 0;
         curPos = jjMoveStringLiteralDfa0_5();
         break;
     }
     if (jjmatchedKind != 0x7fffffff)
     {
        if (jjmatchedPos + 1 < curPos)
           input_stream.backup(curPos - jjmatchedPos - 1);
        if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
        {
           matchedToken = jjFillToken();
           matchedToken.specialToken = specialToken;
       if (jjnewLexState[jjmatchedKind] != -1)
         curLexState = jjnewLexState[jjmatchedKind];
           return matchedToken;
        }
        else if ((jjtoSkip[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
        {
           if ((jjtoSpecial[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
           {
              matchedToken = jjFillToken();
              if (specialToken == null)
                 specialToken = matchedToken;
              else
              {
                 matchedToken.specialToken = specialToken;
                 specialToken = (specialToken.next = matchedToken);
              }
           }
         if (jjnewLexState[jjmatchedKind] != -1)
           curLexState = jjnewLexState[jjmatchedKind];
           continue EOFLoop;
        }
      if (jjnewLexState[jjmatchedKind] != -1)
        curLexState = jjnewLexState[jjmatchedKind];
        curPos = 0;
        jjmatchedKind = 0x7fffffff;
        try {
           curChar = input_stream.readChar();
           continue;
        }
        catch (java.io.IOException e1) { }
     }
     int error_line = input_stream.getEndLine();
     int error_column = input_stream.getEndColumn();
     String error_after = null;
     boolean EOFSeen = false;
     try { input_stream.readChar(); input_stream.backup(1); }
     catch (java.io.IOException e1) {
        EOFSeen = true;
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
        if (curChar == '\n' || curChar == '\r') {
           error_line++;
           error_column = 0;
        }
        else
           error_column++;
     }
     if (!EOFSeen) {
        input_stream.backup(1);
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
     }
     throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
   }
  }
}

}
