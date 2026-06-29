@import hexlet.code.dto.UrlsIndexPage
@param UrlsIndexPage page

@template.layout.page(
page = page,
content = @`
    <h1>Список URL</h1>

    <table class="table table-bordered" data-test="urls">
        <thead>
        <tr>
            <th>ID</th>
            <th>Имя</th>
            <th>Дата создания</th>
            <th>Код ответа</th>
        </tr>
        </thead>
        <tbody>
        @if(page.getUrls().isEmpty())
            <tr><td colspan="4" class="text-center">Нет добавленных страниц</td></tr>
        @else
            @for(var url : page.getUrls())
                <tr>
                    <td>${url.getId()}</td>
                    <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                    <td>${url.getCreatedAtFormatted()}</td>
                    <td><!-- позже добавим код ответа --></td>
                </tr>
            @endfor
        @endif
        </tbody>
    </table>
`
)