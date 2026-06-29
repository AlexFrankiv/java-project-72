@import hexlet.code.dto.UrlShowPage
@param UrlShowPage page

@template.layout.page(
page = page,
content = @`
    <h1>Просмотр URL</h1>

    <table class="table table-bordered" data-test="url">
        <tbody>
        <tr>
            <td>ID</td>
            <td>${page.getUrl().getId()}</td>
        </tr>
        <tr>
            <td>Имя</td>
            <td>${page.getUrl().getName()}</td>
        </tr>
        <tr>
            <td>Дата создания</td>
            <td>${page.getUrl().getCreatedAtFormatted()}</td>
        </tr>
        </tbody>
    </table>
`
)