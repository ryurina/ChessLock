package app.chesslock.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.chesslock.R
import com.github.bhlangonijr.chesslib.Board
import com.github.bhlangonijr.chesslib.Piece
import com.github.bhlangonijr.chesslib.PieceType
import com.github.bhlangonijr.chesslib.Side
import com.github.bhlangonijr.chesslib.Square

private val LightSq = Color(0xFFEEEED2)
private val DarkSq = Color(0xFF769656)
private val HiLight = Color(0xFFF7F769)
private val HiDark = Color(0xFFBACA2B)

private fun pieceRes(p: Piece): Int {
    val w = p.pieceSide == Side.WHITE
    return when (p.pieceType) {
        PieceType.KING -> if (w) R.drawable.wk else R.drawable.bk
        PieceType.QUEEN -> if (w) R.drawable.wq else R.drawable.bq
        PieceType.ROOK -> if (w) R.drawable.wr else R.drawable.br
        PieceType.BISHOP -> if (w) R.drawable.wb else R.drawable.bb
        PieceType.KNIGHT -> if (w) R.drawable.wn else R.drawable.bn
        else -> if (w) R.drawable.wp else R.drawable.bp
    }
}

/** Classic tournament-style board: green and cream squares, standard pieces, coordinates. */
@Composable
fun BoardView(
    board: Board, tick: Int, whiteView: Boolean, selected: Square?,
    targets: Set<Square>, last: Pair<Square, Square>?, onTap: (Square) -> Unit
) {
    Column(Modifier.fillMaxWidth().border(2.dp, Color(0xFF333333))) {
        for (r in 0..7) Row(Modifier.fillMaxWidth()) {
            for (c in 0..7) {
                val f = if (whiteView) c else 7 - c
                val rk = if (whiteView) 7 - r else r
                val sq = Square.squareAt(rk * 8 + f)
                val dark = (f + rk) % 2 == 0
                val hi = sq == selected || (last != null && (sq == last.first || sq == last.second))
                val bg = if (hi) (if (dark) HiDark else HiLight) else if (dark) DarkSq else LightSq
                val label = if (dark) LightSq else DarkSq
                val p = board.getPiece(sq)
                Box(Modifier.weight(1f).aspectRatio(1f).background(bg).clickable { onTap(sq) }) {
                    if (c == 0) Text("${rk + 1}", Modifier.align(Alignment.TopStart).padding(2.dp),
                        color = label, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    if (r == 7) Text("${'a' + f}", Modifier.align(Alignment.BottomEnd).padding(2.dp),
                        color = label, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    if (p != Piece.NONE) Image(painterResource(pieceRes(p)), null, Modifier.fillMaxSize().padding(2.dp))
                    if (sq in targets) Box(Modifier.align(Alignment.Center).size(14.dp).background(Color(0x66000000), CircleShape))
                }
            }
        }
    }
}
