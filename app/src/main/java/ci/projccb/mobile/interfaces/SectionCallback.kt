package ci.projccb.mobile.interfaces


/**
 * Created by Didier BOKA, email: didierboka.developer@gmail.com
 * on 10/04/2022.
 **/

interface SectionCallback {

    fun isSectionHeader(position: Int): Boolean

    fun getSectionHeaderName(postion: Int): String?
}
