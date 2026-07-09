@import hexlet.code.dto.UrlsIndexPage
@import hexlet.code.model.UrlCheck
@import java.time.format.DateTimeFormatter

@param UrlsIndexPage page

!{var formatTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");}

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
            <th>Последняя проверка</th>
            <th>Код ответа</th>
        </tr>
        </thead>
        <tbody>
        @if(page.getUrls().isEmpty())
            <tr><td colspan="5" class="text-center">Нет добавленных страниц</td></tr>
        @else
            @for(var url : page.getUrls())
                <tr>
                    <td>${url.getId()}</td>
                    <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                    <td>${url.getCreatedAtFormatted()}</td>

                    @if(page.getLastCheck(url) != null)
                        <td>${page.getLastCheck(url).getCreatedAt().toLocalDateTime().format(formatTime)}</td>
                        <td>${page.getLastCheck(url).getStatusCode()}</td>
                    @else
                        <td></td>
                        <td></td>
                    @endif
                </tr>
            @endfor
        @endif
        </tbody>
    </table>
`
)