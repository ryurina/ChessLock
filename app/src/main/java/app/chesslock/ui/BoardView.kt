package app.chesslock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.PieceType
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square

private fun glyph(p: Piece) = when (p.pieceType) {
    PieceType.KING -> "♚"; PieceType.QUEEN -> "♛"; PieceType.ROOK -> "♜"
    PieceType.BISHOP -> "♝"; PieceType.KNIGHT -> "♞"; else -> "♟"
} + "\uFE0E" // force text style, not emoji

@Composable
fun BoardView(
    board: Board, tick: Int, whiteView: Boolean, selected: Square?,
    targets: Set<Square>, last: Pair<Square, Square>?, onTap: (Square) -> Unit
) {
    Column(Modifier.fillMaxWidth().brutal(B.Ink, 8.dp, 4.dp)) {
        for (r in 0..7) Row(Modifier.fillMaxWidth()) {
            for (c in 0..7) {
                val f = if (whiteView) c else 7 - c
                val rk = if (whiteView) 7 - r else r
                val sq = Square.squareAt(rk * 8 + f)
                val dark = (f + rk) % 2 == 0
                val bg = when {
                    sq == selected -> B.Green
                    last != null && (sq == last.first || sq == last.second) -> B.Pink
                    dark -> B.Blue
                    else -> Color(0xFFFFF1A8)
                }
                val p = board.getPiece(sq)
                Box(
                    Modifier.weight(1f).aspectRatio(1f).background(bg).clickable { onTap(sq) },
                    contentAlignment = Alignment.Center
                ) {
                    if (p != Piece.NONE) Text(
                        glyph(p), fontSize = 30.sp,
                        color = if (p.pieceSide == Side.WHITE) Color.White else Color.Black,
                        style = TextStyle(shadow = Shadow(Color.Black, Offset(3f, 3f), 0f))
                    )
                    if (sq in targets) Box(Modifier.size(12.dp).background(B.Ink))
                }
            }
        }
    }
}
